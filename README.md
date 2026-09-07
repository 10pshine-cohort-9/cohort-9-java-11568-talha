# 📇 Contact Management System (CMS)

![Version](https://img.shields.io/badge/version-1.0.0-blue) ![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.1-brightgreen) ![React](https://img.shields.io/badge/React-18-61DAFB) ![Vite](https://img.shields.io/badge/Vite-5.0-646CFF) 

A secure, full-stack, web-based contact management system built as the final assignment for the **10Pearls Java Fullstack Internship (Cohort 9)** by **Talha Niazi**.

This application allows users to register, securely log in, manage their profile, and perform full CRUD (Create, Read, Update, Delete) operations on their contacts with paginated listings, search filters, and import/export capabilities.

---

## 🚀 Key Features

* **User Authentication**: Secure Registration, Login, and Password Change mechanisms using stateless JWTs stored securely in `HttpOnly` cookies.
* **Comprehensive Contact Management**: Full CRUD capabilities for contacts. Each contact can have multiple emails (Work, Personal) and multiple phone numbers (Home, Mobile).
* **Search & Pagination**: Server-side pagination and real-time search filtering ensure the dashboard remains blazing fast even with hundreds of contacts.
* **Bulk Import & Export**: Users can easily export their contacts to `.csv` or `.vcf` (vCard) files, and bulk-import contacts using a drag-and-drop file uploader.
* **Modern, Polished UI/UX**: Designed with a responsive, premium Glassmorphism aesthetic. Includes subtle micro-animations, loading skeletons, and interactive hover states.

---

## 🏆 Internship Requirements Achieved

This project was meticulously built to adhere to the strict guidelines set forth by the internship:
* **CodeRabbit Strict Guidelines**: Implemented a global `@RestControllerAdvice` exception handler. All potentially failing operations (database queries, network requests, JSON parsing, file I/O) are properly wrapped, validated, and safely managed without leaking stack traces.
* **SonarQube Quality Passes**: The codebase has been rigorously tested and refactored to resolve code smells, eliminate duplicate declarations, and strictly enforce Object-Oriented Principles (OOP).
* **Advanced Security**: Robust protection against CSRF and XSS attacks. The backend issues an `XSRF-TOKEN` cookie, which the Axios frontend automatically reads and returns in the `X-XSRF-TOKEN` header.

---

## 🛠️ Technology Stack

### Backend
* **Language & Framework**: Java 17, Spring Boot 3
* **Security**: Spring Security (Stateless JWT Authentication, CSRF Protection)
* **Data Access**: Spring Data JPA & Hibernate
* **Database**: H2 In-Memory Database (zero-config local setup)
* **Libraries**: OpenCSV & Ezvcard (for data processing), Lombok

### Frontend
* **Framework**: React.js (Bootstrapped with Vite)
* **HTTP Client**: Axios (Configured for credentials and XSRF headers)
* **Styling**: Vanilla CSS with comprehensive CSS Variables (Light/Dark themes, Glassmorphism, animations)

---

## 🗄️ Database Schema & Relational Integrity

The backend utilizes a robust relational schema mapped via JPA/Hibernate. It uses cascading deletes (`orphanRemoval = true`) to ensure no orphaned records remain in the database when a user or contact is deleted.

* `USERS` (1) ➔ `CONTACTS` (Many)
* `CONTACTS` (1) ➔ `EMAILS` (Many)
* `CONTACTS` (1) ➔ `PHONES` (Many)

---

## 📡 REST API Reference

### Authentication
* `POST /api/auth/register` - Register a new account
* `POST /api/auth/login` - Authenticate and receive `HttpOnly` JWT cookie
* `POST /api/auth/logout` - Clear authentication cookie
* `GET /api/auth/me` - Retrieve authenticated user's profile
* `POST /api/auth/change-password` - Update user password

### Contacts
* `GET /api/contacts?page=0&size=10&search=...` - Retrieve paginated/searchable contacts
* `POST /api/contacts` - Create a new contact
* `GET /api/contacts/{id}` - Retrieve a specific contact
* `PUT /api/contacts/{id}` - Update a contact's details
* `DELETE /api/contacts/{id}` - Delete a contact

### Import / Export
* `GET /api/contacts/export/csv` - Download contacts as CSV
* `GET /api/contacts/export/vcf` - Download contacts as vCard
* `POST /api/contacts/import` - Upload a CSV/VCF file to bulk import

---

## ⚙️ Getting Started (Local Development)

### Prerequisites
* Java 17+ installed
* Node.js 18+ installed

### 1. Running the Backend
1. Open a terminal and navigate to the backend folder:
   ```bash
   cd backend
   ```
2. Run the Spring Boot application (using the included Maven wrapper):
   ```bash
   ./mvnw spring-boot:run
   ```
3. The API will start on `http://localhost:8080`.

### 2. Running the Frontend
1. Open a new terminal window and navigate to the frontend folder:
   ```bash
   cd frontend
   ```
2. Install the Node dependencies:
   ```bash
   npm install
   ```
3. Start the Vite development server:
   ```bash
   npm run dev
   ```
4. Open your browser and navigate to `http://localhost:5173`.

---

## 🧪 Testing

The backend includes a comprehensive suite of **40+ JUnit 5 Unit & Integration Tests** covering Repositories, Services, and Controllers (utilizing Spring Security MockMvc).

To execute the test suite and verify 0% errors, run:
```bash
cd backend
./mvnw test
```
