# SC2002 Hospital Management System

## Project Overview
The **Hospital Management System** is an application aimed at automating the management of hospital operations, including **patient management**, **appointment scheduling**, **staff management**, and **pharmacist operations**. 

By leveraging Java and incorporating core object-oriented programming concepts and SOLID design concepts, the system is designed to:
- Facilitate efficient management of hospital resources.
- Enhance patient care.
- Streamline administrative processes.

This project emphasizes collaborative teamwork to create a functional, user-friendly application that addresses the diverse needs of a modern healthcare facility.

## Contributors

| Name            | Email                      | Git ID            |
|------------------|----------------------------|-------------------|
| Aryaman Agarwal | aryaman005@e.ntu.edu.sg    | aryaman-agarwal   |
| Grisha Agarwala | grisha001@e.ntu.edu.sg     | grishaag18        |
| Aryan Jain      | aryan017@e.ntu.edu.sg      | aryanjain285      |
| Vihaan Motwani  | vihaan002@e.ntu.edu.sg     | VihaanMotwani     |
| Alok Vernekar   | alokaash001@e.ntu.edu.sg   | TinySnowy         |


## Prerequisites

Ensure you have the following installed on your system:

- **JDK 22 or higher**: [Download here](https://www.oracle.com/java/technologies/javase/jdk22-archive-downloads.html) 

Make sure your environment is set up correctly by verifying the installation of Java. You can check this by running the following command:

```bash
java --version
```
Clone repository using [github link](https://github.com/TinySnowy/SC2002HMS)

## Project Design and Implementation

The project application was designed using **Object-Oriented Programming (OOP)** principles, ensuring a robust, maintainable, and modular foundation. Key elements and steps in the design process include:

### Key OOP Principles Implemented
1. **Encapsulation**:  
   - Protected critical data by restricting direct access and providing controlled access through methods.
2. **Inheritance**:  
   - Promoted code reuse by inheriting common functionalities across related classes.
3. **Polymorphism**:  
   - Enabled flexible and dynamic behaviors through method overriding and overloading.
4. **Abstraction**:  
   - Simplified complexity by hiding implementation details and exposing only essential features.

### Key Design Steps

1. **UML Class Diagram**:
   - Designed using **SOLID design principles**, ensuring:
     - Modularity for easy code management and debugging.
     - Scalability to adapt to future system expansions.
2. **Data Handling**:
   - Implemented persistent data storage using **CSV files**.
   - Enabled the program to read, write, and resume smoothly during successive operations.
   - Added additional data columns dynamically as per requirements.
3. **Error Handling and Usability**:
   - Built comprehensive **error-checking mechanisms** to ensure system robustness.
   - Designed a **user-friendly interface** to guide users and effectively handle erroneous inputs.

### SOLID Principles vs. MVC Design

1. **Preferred SOLID Principles over MVC**:
   - Simplified **dependency management**.
   - Better suited for **real-time data access** requirements, especially in systems like a Hospital Management System (HMS).
   - Avoided tightly coupled dependencies and complex interactions commonly found in MVC architectures.
2. **Real-Time Data Needs**:
   - Prioritized **real-time synchronization** for patient records and appointment bookings.
   - Ensured efficient and timely updates for critical operations such as patient care and scheduling.

### Achievements
- **Balanced Design**: By adhering to OOP and SOLID principles, the system achieved a balance between simplicity, functionality, and scalability.
- **Scalable Architecture**: The modular design supports future feature additions with minimal disruption.
- **Enhanced Usability**: A robust, user-friendly interface ensures smooth operations for end-users.


## UML Class Diagram
![UML](https://github.com/user-attachments/assets/d33ca529-2c44-48ea-b996-ed8aa0500664)


