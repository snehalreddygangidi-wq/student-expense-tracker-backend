# Student Expense Tracker - Backend (Spring Boot)

A secure REST API backend for the **Student Expense Tracker** application built using **Java 17**, **Spring Boot 3**, **Spring Security (JWT)**, and **MySQL**.

## Tech Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.4
- **Security**: Spring Security with JWT (JSON Web Tokens) & BCrypt password hashing
- **ORM / Persistence**: Spring Data JPA / Hibernate
- **Database**: MySQL 8.0
- **Build Tool**: Maven

## Architecture & Features
- **Stateless Authentication**: JWT tokens for user registration and login.
- **Role & User Segregation**: Each student only sees and manages their own expenses.
- **CORS Configured**: Ready to connect with modern decoupled frontends (React / Vite).
- **CRUD REST APIs**:
  - Add new expenses with categories (Food, Travel, Books, Fees, Entertainment, etc.).
  - Retrieve user expense history.
  - Delete expenses by ID.
  - Dashboard analytics: Total spent, monthly total, expense counts, and category breakdown.

## REST API Endpoints

### Authentication
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `POST` | `/api/auth/register` | Register new student account | Public |
| `POST` | `/api/auth/login` | Authenticate & return JWT token | Public |
| `POST` | `/api/auth/logout` | Invalidate token/logout | Authenticated |
| `GET` | `/api/auth/me` | Fetch currently logged in student profile | Authenticated |

### Expenses
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/api/expenses` | List all expenses for current student | Authenticated |
| `POST` | `/api/expenses` | Create a new expense entry | Authenticated |
| `DELETE` | `/api/expenses/{id}` | Delete an expense entry | Authenticated |

### Analytics / Dashboard
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/api/dashboard` | Summary statistics (Total, Monthly, Count) | Authenticated |

## Setup & Running Locally

1. **Clone the repository**:
   ```bash
   git clone https://github.com/snehalreddygangidi-wq/student-expense-tracker-backend.git
   cd student-expense-tracker-backend
   ```

2. **Configure Database**:
   Make sure MySQL is running and create the database:
   ```sql
   CREATE DATABASE student_expense_tracker;
   ```
   Update your database credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/student_expense_tracker?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=your_mysql_password
   ```

3. **Build & Run**:
   ```bash
   mvn spring-boot:run
   ```
   The backend will start at: `http://localhost:8080`.
