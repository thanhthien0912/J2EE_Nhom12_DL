# VPS Quick Deploy (Docker + Nginx + Atlas + Let's Encrypt)

Huong dan nhanh de chay full project (frontend + backend) tren VPS bang Docker, dung MongoDB Atlas, Nginx reverse proxy va HTTPS free bang Let's Encrypt.

## 1) Clone source

```bash
git clone https://github.com/thanhthien0912/J2EE_Nhom12_DL.git
cd J2EE_Nhom12_DL
```

## 2) Tro domain ve VPS

Tao 2 ban ghi DNS `A` tro ve IP VPS:

- `demo-java.honeysocial.click`
- `api1.honeysocial.click`

Neu dung Cloudflare, co the bat proxy sau khi record da resolve dung.

## 3) Tao file env

```bash
cp .env.docker.example .env
nano .env
```

Cap nhat toi thieu cac bien sau trong `.env`:

```env
APP_FRONTEND_URL=https://demo-java.honeysocial.click
APP_BACKEND_URL=https://api1.honeysocial.click
APP_CORS_ALLOWED_ORIGINS=https://demo-java.honeysocial.click
FRONTEND_DOMAIN=demo-java.honeysocial.click
BACKEND_DOMAIN=api1.honeysocial.click
VITE_API_BASE_URL=https://api1.honeysocial.click/api
VITE_BACKEND_URL=https://api1.honeysocial.click
LETSENCRYPT_EMAIL=<your-email@example.com>
LETSENCRYPT_STAGING=0
GOOGLE_CLIENT_ID=<your-google-client-id>
GOOGLE_CLIENT_SECRET=<your-google-client-secret>
GOOGLE_REDIRECT_URI=https://api1.honeysocial.click/login/oauth2/code/google
JWT_SECRET=<your-strong-jwt-secret>
MONGODB_URI=mongodb+srv://<username>:<password>@<cluster-url>/nhom12?retryWrites=true&w=majority&appName=Cluster0
```

Dat `LETSENCRYPT_STAGING=1` neu muon test cap cert truoc khi lay cert that.
Dam bao IP public cua VPS da duoc them trong MongoDB Atlas > Network Access.

## 4) Mo cong tren VPS (neu dung UFW)

```bash
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw reload
```

## 5) Chay app

```bash
docker compose up -d --build
```

Lenh tren se boot Nginx voi cert tam thoi de stack len duoc ngay. Sau do cap cert Let's Encrypt:

```bash
chmod +x scripts/init-letsencrypt.sh scripts/renew-letsencrypt.sh
./scripts/init-letsencrypt.sh
```

Truy cap:

- Frontend: `https://demo-java.honeysocial.click`
- Backend API: `https://api1.honeysocial.click/api`

Nginx doc config trong `nginx/conf.d/default.conf` va luu cert trong `nginx/certbot/conf/`.

## 6) Cau hinh Google OAuth (bat buoc)

Trong Google Cloud Console > OAuth Client:

- Authorized redirect URI:
  `https://api1.honeysocial.click/login/oauth2/code/google`
- Neu app o trang thai Testing, them Gmail cua ban vao Test users.

## 7) Lenh quan trong

```bash
# Xem trang thai
docker compose ps

# Xem log realtime
docker compose logs -f

# Chi xem log backend
docker compose logs -f backend

# Chi xem log nginx
docker compose logs -f nginx

# Renew cert thu cong
./scripts/renew-letsencrypt.sh

# Restart nhanh
docker compose restart

# Stop toan bo
docker compose down

# Cap nhat code va deploy lai
git pull
docker compose up -d --build
```

Neu backend khong ket noi duoc Atlas, kiem tra nhanh:

- VPS IP da duoc them trong MongoDB Atlas > Network Access
- User trong MongoDB Atlas > Database Access con dung mat khau va quyen
- `MONGODB_URI` trong `.env` khong bi xuong dong hay thua ky tu

Tu dong renew moi dem luc 3h sang:

```bash
crontab -e
```

```cron
0 3 * * * cd /root/J2EE_Nhom12_DL && ./scripts/renew-letsencrypt.sh >> /var/log/nhom12-letsencrypt.log 2>&1
```

## 8) Neu bi loi `google_failed`

Kiem tra 4 diem sau:

1. `GOOGLE_REDIRECT_URI` trong `.env` phai giong 100% voi Google Console.
2. `APP_FRONTEND_URL` phai la `https://demo-java.honeysocial.click`.
3. Truy cap bang domain dung nhu da khai bao, khong dung IP noi bo.
4. Chay lai stack sau khi sua env:

```bash
docker compose down
docker compose up -d --build
./scripts/init-letsencrypt.sh
```
