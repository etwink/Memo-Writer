# Corporate Memo Management System

A Spring Boot web application for creating, managing, and storing corporate office memos.

## Features

- **Create Memos**: Write new memos with title, sender, recipients, and content
- **Edit Memos**: Modify existing memos
- **View Memos**: Browse all memos and view individual memo details
- **Delete Memos**: Remove memos from the system
- **Download as Word**: Export memos as professionally formatted .docx files
- **Organize Recipients**: Support for primary recipients (To), carbon copies (CC), and sender information
- **Attachments**: Track attachments associated with each memo
- **Timestamps**: Automatic tracking of creation and update dates
- **NoSQL Database**: MongoDB integration for flexible document storage

## Technologies Used

- **Framework**: Spring Boot 3.1.5
- **Language**: Java 17
- **Template Engine**: Thymeleaf
- **NoSQL Database**: MongoDB
- **Document Generation**: Apache POI (for .docx export)
- **Build Tool**: Maven

## Project Files

- **`.gitignore`**: Specifies files and directories that should be ignored by Git version control
- **`.envtemplate`**: Template file containing environment variable examples for easy setup
- **`docker-compose.yml`**: Docker configuration for running MongoDB in a container
- **`pom.xml`**: Maven project configuration with all dependencies

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6.0 or higher
- MongoDB 4.0 or higher (running locally or accessible)

### Environment Setup

1. Copy the `.envtemplate` file to `.env`:
   ```bash
   cp .envtemplate .env
   ```

2. Edit the `.env` file with your specific configuration values:
   - Update MongoDB connection string for your environment
   - Modify any other settings as needed

3. The application will automatically load environment variables from the `.env` file

### Installation

1. Open the project in your IDE or command line
2. Build the project:
   ```
   mvn clean install
   ```

### Running the Application

1. Run the Spring Boot application:
   ```
   mvn spring-boot:run
   ```

2. Open your browser and navigate to:
   ```
   http://localhost:8080
   ```

### Using the Application

1. **Home Page**: The application redirects to `/memos` where you can see all memos
2. **Create Memo**: Click "Create New Memo" to write a new memo
3. **Fill in Fields**:
   - **Title**: Memo subject
   - **From**: Sender's name
   - **To**: Primary recipient(s) - comma-separated for multiple
   - **CC**: Carbon copy recipient(s) - comma-separated for multiple
   - **Memo Body**: The content of the memo
   - **Attachments**: List of files (comma-separated)
4. **Save**: Click "Create Memo" to save the new memo
5. **Download**: Click "Download as Word" button to export the memo as a professionally formatted .docx file
6. **Manage**: Use the memo cards to view, edit, or delete existing memos

## API/URL Endpoints

| Method | Endpoint | Action |
|--------|----------|--------|
| GET | / | Redirect to memo list |
| GET | /memos | View all memos |
| GET | /memos/new | Show create memo form |
| POST | /memos | Save new memo |
| GET | /memos/{id} | View specific memo |
| GET | /memos/edit/{id} | Show edit form |
| POST | /memos/{id} | Update memo |
| GET | /memos/delete/{id} | Delete memo |
| GET | /memos/download/{id} | Download memo as .docx file |

## Database

The application uses MongoDB for document storage. The Memo collection is automatically created by Spring Data MongoDB.

### MongoDB Connection String Format
```
mongodb://[username:password@]localhost:27017/memo_database[?options]
```

For a default local MongoDB installation, the connection string is already configured:
```
spring.data.mongodb.uri=mongodb://localhost:27017/memo_database
```

## Features to Consider for Future Development

- User authentication and authorization
- Email integration for sending memos
- File upload functionality for attachments
- Search and filter memos
- Memo templates
- Archive functionality
- Distribution lists for common recipients
- Digital signatures
- PDF export

## Notes

- MongoDB must be running and accessible at the configured connection string for the application to work
- The application uses MongoDB's ObjectId for document IDs
- File attachments are currently stored as text (file names/paths only) - implement actual file upload/storage for production
- Downloaded .docx files are automatically formatted as professional memos with proper spacing and formatting
- For production deployments, use MongoDB Atlas or a managed MongoDB service for better scalability and security

## License

This project is open source and available for corporate use.
