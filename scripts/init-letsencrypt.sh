#!/bin/sh
set -eu

ROOT_DIR=$(CDPATH= cd -- "$(dirname "$0")/.." && pwd)
cd "$ROOT_DIR"

if [ -f .env ]; then
  set -a
  . ./.env
  set +a
fi

extract_host() {
  value="$1"
  value=${value#http://}
  value=${value#https://}
  value=${value%%/*}
  printf '%s' "$value"
}

require_value() {
  name="$1"
  value="$2"
  if [ -z "$value" ]; then
    printf 'Missing required value: %s\n' "$name" >&2
    exit 1
  fi
}

is_letsencrypt_cert() {
  cert_path="$1"
  [ -s "$cert_path" ] && openssl x509 -in "$cert_path" -noout -issuer 2>/dev/null | grep -q "Let's Encrypt"
}

request_cert() {
  domain="$1"
  live_path="$ROOT_DIR/nginx/certbot/conf/live/$domain"

  if is_letsencrypt_cert "$live_path/fullchain.pem"; then
    printf "%s\n" "Skipping $domain: valid Let's Encrypt certificate already exists."
    return
  fi

  rm -rf "$ROOT_DIR/nginx/certbot/conf/live/$domain"
  rm -rf "$ROOT_DIR/nginx/certbot/conf/archive/$domain"
  rm -f "$ROOT_DIR/nginx/certbot/conf/renewal/$domain.conf"

  staging_arg=
  if [ "${LETSENCRYPT_STAGING:-0}" != "0" ]; then
    staging_arg="--staging"
  fi

  docker compose --profile tools run --rm certbot certonly \
    --webroot \
    -w /var/www/certbot \
    $staging_arg \
    --email "$LETSENCRYPT_EMAIL" \
    --agree-tos \
    --no-eff-email \
    --rsa-key-size 4096 \
    --cert-name "$domain" \
    -d "$domain"
}

frontend_domain=$(extract_host "${APP_FRONTEND_URL:-https://demo-java.honeysocial.click}")
backend_domain=$(extract_host "${APP_BACKEND_URL:-https://api1.honeysocial.click}")

require_value "LETSENCRYPT_EMAIL" "${LETSENCRYPT_EMAIL:-}"
require_value "APP_FRONTEND_URL" "$frontend_domain"
require_value "APP_BACKEND_URL" "$backend_domain"

mkdir -p "$ROOT_DIR/nginx/certbot/conf" "$ROOT_DIR/nginx/certbot/www"

docker compose up -d --build frontend backend nginx

request_cert "$frontend_domain"
request_cert "$backend_domain"

docker compose exec -T nginx nginx -s reload

printf 'HTTPS is ready for %s and %s\n' "$frontend_domain" "$backend_domain"
