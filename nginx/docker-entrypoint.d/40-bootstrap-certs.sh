#!/bin/sh
set -eu

bootstrap_cert() {
  domain="$1"
  cert_dir="/etc/letsencrypt/live/$domain"

  if [ -s "$cert_dir/fullchain.pem" ] && [ -s "$cert_dir/privkey.pem" ]; then
    return
  fi

  mkdir -p "$cert_dir"
  openssl req -x509 -nodes -newkey rsa:2048 -days 1 \
    -keyout "$cert_dir/privkey.pem" \
    -out "$cert_dir/fullchain.pem" \
    -subj "/CN=$domain" >/dev/null 2>&1
}

bootstrap_cert "${FRONTEND_DOMAIN:-demo-java.honeysocial.click}"
bootstrap_cert "${BACKEND_DOMAIN:-api1.honeysocial.click}"
