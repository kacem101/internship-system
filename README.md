# 🎓 ENSCS Internship Management System

A modern, desktop-based platform designed for the **National School of Cybersecurity (ENSCS)** to streamline the internship application process. This system connects students seeking professional experience with supervisors offering internship opportunities.

---

## ✨ Features

### 👨‍🎓 For Students
- **Browse & Filter:** Search for internship offers by title, company, or technical tags.
- **Detailed Insights:** View comprehensive descriptions and requirements for each position.
- **Easy Application:** Apply to offers with a single click and track your application status in real-time.
- **Application History:** Keep a record of all your past and current applications.

### 👨‍💼 For Supervisors
- **Offer Management:** Create, edit, and close internship postings.
- **Applicant Review:** View a centralized list of students who applied for your offers.
- **Decision Matrix:** Approve or reject applications directly from the dashboard.
- **Company Branding:** Maintain a consistent company profile across all postings.

### 🎨 Modern UI/UX
- **Tailwind-Inspired CSS:** A custom JavaFX stylesheet that brings a utility-first web aesthetic (Slate/Emerald palette) to a desktop environment.
- **Responsive Layouts:** Uses a Master-Detail pattern for intuitive navigation.

---

## 🛠️ Tech Stack

- **Language:** Java 17+
- **Framework:** JavaFX 17
- **Build Tool:** Maven
- **UI Styling:** Custom JavaFX CSS (Tailwind-inspired)
- **Data Persistence:** JSON-based local storage

---

## 📁 Project Structure

```text
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.enscs.internship
│   │   │       ├── controllers  # FXML Logic & Event Handlers
│   │   │       ├── core         # Application Entry & Main logic
│   │   │       ├── models       # Data objects (Student, Supervisor, Offer)
│   │   │       └── services     # Business logic & Data Persistence
│   │   └── resources
│   │       ├── css          # Tailwind-inspired stylesheets
│   │       ├── data         # Local JSON data files
│   │       └── fxml         # UI Layout definitions
└── pom.xml                  # Maven dependencies
