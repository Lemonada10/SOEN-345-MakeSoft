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

# Make Sign-in work from the Vercel site (deploy backend)
The Vercel frontend calls the API at `REACT_APP_API_BASE`. If unset, it uses `http://localhost:8080`, so sign-in fails when you're not running the backend locally. To fix:

1. **Deploy the backend** to a cloud host (e.g. [Railway](https://railway.app), [Render](https://render.com), or [Fly.io](https://fly.io)).
   - Build: from repo root, run `cd project && mvn -DskipTests package`; the JAR is `project/target/project-0.0.1-SNAPSHOT.jar`.
   - Run the JAR with Java 17. Set env vars for the database: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` (e.g. your Neon PostgreSQL URL and credentials).
2. **In Vercel:** **Project Settings → Environment Variables** → add `REACT_APP_API_BASE` = `https://YOUR-DEPLOYED-BACKEND-URL/api` (no trailing slash).
3. **Redeploy** the frontend on Vercel so it picks up the new env var.

The backend is already configured to allow requests from your Vercel domain (CORS).
