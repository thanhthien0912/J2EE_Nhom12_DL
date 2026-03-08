#!/bin/sh
set -eu

ROOT_DIR=$(CDPATH= cd -- "$(dirname "$0")/.." && pwd)
cd "$ROOT_DIR"

docker compose --profile tools run --rm certbot renew --quiet
docker compose exec -T nginx nginx -s reload

printf 'Certificate renewal check completed.\n'
