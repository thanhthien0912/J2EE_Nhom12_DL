# VPS Quick Deploy (Docker)

Huong dan nhanh de chay full project (frontend + backend + mongo) tren VPS bang Docker.

## 1) Clone source

```bash
git clone https://github.com/thanhthien0912/J2EE_Nhom12_DL.git
cd J2EE_Nhom12_DL
```

## 2) Tao file env

```bash
cp .env.docker.example .env
nano .env
```

Cap nhat toi thieu cac bien sau trong `.env`:

```env
APP_FRONTEND_URL=http://<PUBLIC_IP>:5173
GOOGLE_CLIENT_ID=<your-google-client-id>
GOOGLE_CLIENT_SECRET=<your-google-client-secret>
GOOGLE_REDIRECT_URI=http://<PUBLIC_IP>:5173/login/oauth2/code/google
JWT_SECRET=<your-strong-jwt-secret>
MONGODB_URI=<your-mongodb-uri>
```

## 3) Mo cong tren VPS (neu dung UFW)

```bash
sudo ufw allow 5173/tcp
sudo ufw allow 8080/tcp
sudo ufw reload
```

## 4) Chay app (1 lenh)

```bash
docker compose up -d --build
```

Truy cap:

- Frontend: `http://<PUBLIC_IP>:5173`
- Backend API: `http://<PUBLIC_IP>:8080/api`

## 5) Cau hinh Google OAuth (bat buoc)

Trong Google Cloud Console > OAuth Client:

- Authorized redirect URI:
  `http://<PUBLIC_IP>:5173/login/oauth2/code/google`
- Neu app o trang thai Testing, them Gmail cua ban vao Test users.

## 6) Lenh quan trong

```bash
# Xem trang thai
docker compose ps

# Xem log realtime
docker compose logs -f

# Chi xem log backend
docker compose logs -f backend

# Restart nhanh
docker compose restart

# Stop toan bo
docker compose down

# Cap nhat code va deploy lai
git pull
docker compose up -d --build
```

## 7) Neu bi loi `google_failed`

Kiem tra 3 diem sau:

1. `GOOGLE_REDIRECT_URI` trong `.env` phai giong 100% voi Google Console.
2. Truy cap bang `PUBLIC_IP`/domain dung nhu da khai bao (khong dung IP noi bo).
3. Chay lai stack sau khi sua env:

```bash
docker compose down
docker compose up -d --build
```
