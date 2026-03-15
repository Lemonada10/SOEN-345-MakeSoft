# SOEN-345-MakeSoft
Cloud-based Ticket Reservation Application

# run backend
- cd project
- mvn spring-boot:run

# run frontend
- cd frontend
- npm install
- npm install lottie-react
- npm start

# Deploy frontend to Vercel
1. Import this repo in Vercel and click **Deploy**.
2. Before or after first deploy: **Project Settings → General → Root Directory** → set to `frontend` (required).
3. If you have a production backend: **Project Settings → Environment Variables** → add `REACT_APP_API_BASE` = `https://your-backend-url.com/api`.
4. Redeploy if you changed root directory or env vars.
