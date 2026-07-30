# MBI Banking System
**A secure, modern, and containerized digital banking application built with React, Spring Boot, Nginx, and MySQL.**

## 📌 Table of Contents
- <a href="#project-overview">Project Overview</a>
- <a href="#architecture-overview">Architecture Overview</a>
- <a href="#directory-structure">Directory Structure</a>
- <a href="#getting-started">Getting Started</a>
- <a href="#database-configuration">Database Configuration</a>
- <a href="#kubernetes-deployment">Kubernetes Deployment (Alternative)</a>
- <a href="#author--contact">Author & Contact</a>
---

<h2><a class="anchor" id="project-overview"></a>📌 Project Overview</h2>

This project is a secure, modern, and fully containerized digital banking application. It provides an end-to-end banking system with a single page application frontend and a robust REST API backend.

---

![Banking Application Snapshot](images/placeholder_1.png)

---

![Dashboard Preview](images/placeholder_2.png)

## <a class="anchor" id="architecture-overview"></a>🏗️ Architecture Overview

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

## <a class="anchor" id="directory-structure"></a>📁 Directory Structure

```text
├── bank_backend/         # Spring Boot backend source & Dockerfile
├── bank_frontend/        # React/Vite frontend source & Dockerfile
├── nginx/                # Nginx Load Balancer config & Dockerfile
├── docker-compose.yml    # Orchestrates all containers, volumes, and networks
└── README.md             # Project documentation
```

---

## <a class="anchor" id="getting-started"></a>🚀 Getting Started

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

## <a class="anchor" id="database-configuration"></a>⚙️ Database Configuration

The application is configured to preserve transaction history and user records across restarts:
- **`spring.jpa.hibernate.ddl-auto`** is set to **`update`** to ensure tables are not dropped on restart.
- To change database settings, configure the environment variables inside `docker-compose.yml`.

---

## <a class="anchor" id="kubernetes-deployment"></a>🚢 Kubernetes Deployment (Alternative)

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

---
## <a class="anchor" id="author--contact"></a>👥 Author & Contact

**Manoj Bharathi S**

* ✉️ Email: [manojbharathiwork@gmail.com](mailto:manojbharathiwork@gmail.com)
* 🔗 [LinkedIn](https://www.linkedin.com/in/manoj-bharathi)
