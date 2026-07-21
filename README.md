# MBI Banking System

A secure, modern, and containerized digital banking application built with React, Spring Boot, Nginx, and MySQL.

---

## Architecture Overview

This project is fully containerized and orchestrated using **Docker Compose**. It consists of the following services:

```
                       ┌──────────────────────┐
                       │  User Browser (80)   │
                       └──────────┬───────────┘
                                  │
                       ┌──────────▼───────────┐
                       │  Nginx LoadBalancer  │
                       └─────┬──────────┬─────┘
                             │          │
            / (Frontend)     │          │ /auth, /profile, etc. (Backend LB)
                             │          │
             ┌───────────────▼┐        ┌▼────────────────┐
             │ Frontend (Nginx)│        │ Backend Service │
             │  (Port 80/internal)      │  (Port 8000)    │
             └────────────────┘        └────────┬────────┘
                                                │
                                       ┌────────▼────────┐
                                       │  MySQL DB (3306)│
                                       └─────────────────┘
```

- **`frontend`**: Single Page Application (SPA) built using React and Vite, served via Nginx.
- **`backend`**: REST API built with Spring Boot (Java 21) using Spring Data JPA and Hibernate.
- **`loadbalancer`**: Nginx reverse proxy serving the frontend and routing API routes (`/auth`, `/profile`, `/mybalance`, `/deposit`, `/withdraw`, `/transfer`) to the backend.
- **`mysqldb`**: MySQL 8 database storing all persistent data with schema migration safety.

---

## Directory Structure

```text
├── bank_backend/         # Spring Boot backend source & Dockerfile
├── bank_frontend/        # React/Vite frontend source & Dockerfile
├── nginx/                # Nginx Load Balancer config & Dockerfile
├── docker-compose.yml    # Orchestrates all containers, volumes, and networks
└── README.md             # Project documentation
```

---

## Getting Started

### Prerequisites
Make sure you have [Docker](https://www.docker.com/) and Docker Compose installed on your system.

### Running the Application

1. **Start all services**:
   ```bash
   docker compose up --build -d
   ```
   This will build the frontend, backend, and Nginx images, initialize the MySQL database, and run everything in the background.

2. **Access the application**:
   - **Frontend App**: [http://localhost](http://localhost)
   - **Backend API**: [http://localhost/auth/login](http://localhost/auth/login) (via Load Balancer)

3. **Scaling Backend Instances** (Load Balancing):
   To scale the Spring Boot API service across multiple instances:
   ```bash
   docker compose up -d --scale backend=3
   ```
   Nginx will automatically load-balance incoming HTTP requests among all active backend containers.

4. **Stop all services**:
   ```bash
   docker compose down
   ```
   *(Your banking records are safe and persist inside the `mysql_data` Docker volume across restarts)*

---

## Database Configuration

The application is configured to preserve transaction history and user records across restarts:
- **`spring.jpa.hibernate.ddl-auto`** is set to **`update`** to ensure tables are not dropped on restart.
- To change database settings, configure the environment variables inside `docker-compose.yml`.

---

## Kubernetes Deployment (Alternative)

All necessary manifests to run the application in a Kubernetes cluster are provided in the `/k8s` directory:

1. **Deploy all manifests**:
   ```bash
   kubectl apply -f k8s/
   ```

2. **Verify services**:
   ```bash
   kubectl get pods,svc
   ```

3. **Access the application**:
   - The `nginx-loadbalancer` service is of type `LoadBalancer`.
   - Retrieve the external IP:
     ```bash
     kubectl get service nginx-loadbalancer
     ```
   - Access the application at `http://<EXTERNAL-IP>` (or `http://localhost` if using Docker Desktop / Minikube tunnel).

