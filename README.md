# Meal Management App

A comprehensive mess (dining) management system designed for shared living environments like hostels, student accommodations, or group housing. The app manages meals, expenses, deposits, and financial tracking with role-based permissions.

## 📱 Features

### Core Features
- **Meal Management**: Track breakfast, lunch, and dinner for all members
- **Financial Tracking**: Manage purchases, deposits, and expenses
- **Role-Based Access**: Super User, Manager, and Regular User permissions
- **Monthly Reports**: Generate detailed financial and meal reports
- **Member Management**: Add, edit, and manage mess members
- **Multi-Month Support**: Navigate and manage data across different months

### Key Capabilities
- Real-time meal tracking with fractional meal support (0.0 - 1.0)
- Automated meal charge calculations
- Purchase request system for regular users
- Fund management and deposit tracking
- PDF report generation
- Profile management with photo upload
- Settings and configuration management

## 🏗️ Architecture

### Technology Stack
- **Language**: Kotlin
- **Framework**: Android SDK
- **Architecture**: MVVM with Data Binding
- **Dependency Injection**: Hilt
- **Database**: Room Database (SQLite)
- **Networking**: Retrofit with OkHttp
- **UI**: Material Design Components
- **Authentication**: Custom JWT-based system

### Project Structure
```
app/
├── src/main/java/com/logicline/mydining/
│   ├── adapters/          # RecyclerView adapters
│   ├── api/              # Network API interfaces and models
│   ├── models/           # Data models and entities
│   ├── repository/       # Data repository layer
│   ├── ui/
│   │   ├── activities/   # Main activities
│   │   └── fragments/    # UI fragments
│   ├── utils/           # Utility classes and helpers
│   └── viewmodels/      # ViewModel classes
├── src/main/res/
│   ├── drawable/        # Vector drawables and icons
│   ├── layout/          # XML layout files
│   ├── values/          # Colors, strings, styles
│   └── menu/            # Menu resources
└── build.gradle         # App-level build configuration
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK API 21+ (Android 5.0+)
- Java 8 or Kotlin 1.5+
- Firebase account (for cloud services)

### Installation
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd MealManagement
   ```

2. Open the project in Android Studio

3. Sync the project with Gradle files

4. Add your Firebase configuration:
   - Place `google-services.json` in the `app/` directory
   - Configure Firebase Authentication and Cloud Firestore

5. Build and run the app:
   ```bash
   ./gradlew assembleDebug
   ```

### Configuration
1. Update `local.properties` with your local SDK paths
2. Configure signing keys in `app/build.gradle` if needed
3. Set up backend API endpoints in the network configuration

## 👥 User Roles

### Super User (Admin)
- Full system control and access to all features
- Can add/edit/delete members
- Manage all financial operations
- Generate reports and analytics
- Reset mess data and change super user

### Manager
- Most management functions except super user privileges
- Add/edit members and meals
- Manage purchases and deposits
- Generate reports
- Configure basic settings

### Regular User
- Limited to personal meal entries (today/tomorrow only)
- Submit purchase requests
- View own meal history and balance
- Update personal profile

## 📊 Data Models

### Core Entities
- **User**: Member information and authentication
- **Meal**: Daily meal tracking (breakfast, lunch, dinner)
- **Purchase**: Expense and purchase records
- **Deposit**: Member deposit and balance tracking
- **Fund**: Mess fund management
- **Month**: Monthly data organization

### Key Relationships
- Users have many Meals, Purchases, and Deposits
- Months contain all data for a specific time period
- Purchases can be linked to specific members
- Funds are managed at the mess level

## 🔄 User Flow

### Authentication Flow
```
App Launch → Check Updates → Login/Register → Main Dashboard
```

### Main Navigation
```
Home (Dashboard) ← → Month (Reports) ← → Settings (Admin)
```

### Core Operations
1. **Meal Entry**: Home → Add Meal → Select Member/Date → Enter Counts → Save
2. **Purchase Management**: Home → Purchases → Add/Edit/Approve → Update Deposits
3. **Member Management**: Home → Members → Add/Edit/Delete → Role Assignment
4. **Reports**: Month → Select Period → Generate Reports → Export/Share

## 🛠️ Development

### Build System
- Gradle build system with Kotlin DSL
- Multi-module architecture support
- Automated testing and linting
- Release build signing

### Testing
```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run all tests
./gradlew check
```

### Code Style
- Kotlin coding conventions
- Android Kotlin Style Guide
- ktlint for code formatting
- Detekt for static analysis

## 📱 API Documentation

### Authentication Endpoints
- `POST /login` - User authentication
- `POST /register` - New user registration
- `POST /logout` - User logout
- `POST /refresh` - Token refresh

### Core API Endpoints
- `GET /meals` - Retrieve meals data
- `POST /meals` - Add new meal entry
- `GET /purchases` - Get purchases list
- `POST /purchases` - Add new purchase
- `GET /members` - Get mess members
- `POST /members` - Add new member

## 🔒 Security

### Authentication
- JWT-based authentication system
- Secure token storage using Android Keystore
- Session management with automatic refresh
- Role-based access control

### Data Protection
- Local database encryption
- Secure API communication (HTTPS)
- Input validation and sanitization
- Protected against common Android vulnerabilities

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📞 Support

For support and questions:
- Email: support@logicline.com
- GitHub Issues: [Create an issue](../../issues)
- Documentation: [Wiki](../../wiki)

## 📝 Changelog

### Version 1.2.0
- Added month picker functionality
- Enhanced reporting system
- Improved user interface
- Bug fixes and performance improvements

### Version 1.1.9
- Fixed meal calculation issues
- Added purchase request system
- Enhanced member management
- UI/UX improvements

---

**Made with ❤️ by LogicLine Team**
