# 💰 Personal Finance Manager - Backend

A backend service for a **Personal Finance Management** application that helps users track income, expenses, and spending categories.  
These APIs supports authentication, authorization, automated reporting and scheduled email reminders.

---

## 🚀 Features

- **User Management**
  - User registration and login with JWT-based authentication.
  - Email verification for account activation.

- **Expense & Income Tracking**
  - CRUD APIs to manage income and expense entries.
  - Category management for organized tracking.

- **Reports & Analytics**
  - Generate detailed PDF reports of income and expenses.
  - User can also view records based on filters provided.
  - Automatically send reports via email.

- **Automation**
  - Scheduled daily reminders using **Spring Boot cron jobs**.
  - Email notifications via **Brevo** mailing service.

- **Secure RESTful APIs**
  - Follows REST design principles with proper validation and exception handling.
  - Protected endpoints secured with **JWT token authentication**.

---

## 🧩 Tech Stack

| Layer | Technology |
|-------|-------------|
| **Backend Framework** | Java Spring Boot |
| **Database Layer** | Spring Data JPA |
| **Mailing Service** | Brevo |
| **Task Scheduling** | Spring Boot Scheduler / Cron Jobs |
| **Authentication** | JWT (JSON Web Token) |
| **Reporting** | Excel Generation using apache poi library |
| **Build Tool** | Maven |
| **API Testing** | Postman |

---

## ⚙️ Setup Instructions

### Prerequisites
- Java 17+
- Maven
- MySQL or any relational database
- Brevo account for mailing setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/personal-finance-manager-backend.git
   cd personal-finance-manager-backend
Update the application.yml file with:

Database credentials

Brevo API key and mail configurations

JWT secret key

Build and run the project

bash
Copy code
mvn clean install
mvn spring-boot:run
Access the APIs at:

bash
Copy code
http://localhost:8080/api/v1/

---

## 🚀 Personal Finance Manager - Frontend
Link: https://github.com/magdum-adiraj/personalfinancemanager-frontend
     
