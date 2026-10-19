# InventoryPro Android 📦

> A native Android inventory management app built in Kotlin with Firebase,
> published on Google Play Store.

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-API_24+-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-Firestore-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![CI](https://img.shields.io/badge/CI-GitHub_Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)

---

## 📱 About

InventoryPro is a companion Android application for **Durban Small Business
Supplies** — a small retail business in Durban, KwaZulu-Natal, South Africa.

It allows staff to manage inventory, record sales, and view reports directly
from their Android devices, with real-time data powered by Firebase Firestore.

Built as a Final Year **Work Integrated Learning (WIL)** project —
Module XISD6329 — by Team CodeCrafters.

---

## 🌐 Related Links

| Resource | Link |
|---|---|
| Live Website | [inventorypro.github.io](https://Lefakatleho14.github.io/InventoryPro/) |
| Web App Repo | [InventoryPro Web](https://github.com/Lefakatleho14/InventoryPro) |
| Android Repo | [InventoryPro Android](https://github.com/Lefakatleho14/InventoryPro-Android) |

---

## ✨ Features

| Feature | Manager | Shop Assistant |
|---|:---:|:---:|
| Login (Email + Google SSO) | ✅ | ✅ |
| Register new account | ✅ | ✅ |
| Dashboard with live stats | ✅ | ✅ |
| View products | ✅ | ✅ |
| Add / Edit / Delete products | ✅ | ❌ |
| Record sales | ✅ | ✅ |
| View sales history | ✅ | ✅ |
| Share receipt | ✅ | ✅ |
| Inventory valuation report | ✅ | ❌ |
| Low stock report | ✅ | ❌ |
| Change password | ✅ | ✅ |
| Logout | ✅ | ✅ |

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 1.9.22 |
| Minimum SDK | API 24 (Android 7.0) |
| Target SDK | API 34 (Android 14) |
| Architecture | MVVM + Repository Pattern |
| Database | Firebase Firestore |
| Authentication | Firebase Auth (Email + Google SSO) |
| UI | XML Layouts + Material Design 3 |
| Dependency Injection | Manual (Repository pattern) |
| Async | Kotlin Coroutines |
| Image Loading | Glide 4.16 |
| CI/CD | GitHub Actions |
| Project Management | Azure DevOps |

---

## 🗂️ Project Structure

app/src/main/java/com/codecrafters/inventorypro/
├── activities/
│ ├── SplashActivity.kt # Entry point + auth check
│ ├── LoginActivity.kt # Email/password + Google SSO
│ ├── RegisterActivity.kt # New user registration
│ ├── MainActivity.kt # Bottom navigation host
│ ├── AddEditProductActivity.kt # Add and edit products
│ ├── RecordSaleActivity.kt # Record a sale
│ ├── ReceiptActivity.kt # Sale receipt + share
│ ├── SalesHistoryActivity.kt # Full sales history
│ ├── ReportsActivity.kt # Manager reports
│ ├── ChangePasswordActivity.kt # Change password
│ └── SettingsActivity.kt # Settings screen
├── adapters/
│ ├── ProductAdapter.kt # Products RecyclerView
│ ├── ProductSearchAdapter.kt # Sale product search
│ ├── RecentSaleAdapter.kt # Dashboard recent sales
│ └── LowStockAdapter.kt # Low stock report list
├── fragments/
│ ├── DashboardFragment.kt # Dashboard tab
│ ├── ProductsFragment.kt # Products tab
│ ├── SalesFragment.kt # Sales tab
│ ├── ReportsFragment.kt # Reports tab
│ └── SettingsFragment.kt # Settings tab
├── models/
│ ├── User.kt # User data model
│ ├── Product.kt # Product data model
│ └── Sale.kt # Sale data model
├── repository/
│ ├── UserRepository.kt # Firebase Auth operations
│ ├── ProductRepository.kt # Firestore product CRUD
│ └── SaleRepository.kt # Firestore sale operations
└── utils/
├── SessionManager.kt # SharedPreferences session
├── FirestoreHelper.kt # Firestore constants
└── Extensions.kt # Kotlin extension functions


---

## 🗄️ Firebase Collections

Firestore/
├── users/ # User profiles with role field
│ └── {uid} # Document ID = Firebase Auth UID
├── products/ # Inventory products
│ └── {docId} # Auto-generated document ID
└── sales/ # Sale transaction records
└── {docId} # Auto-generated document ID


---

## 🔐 Security

- Passwords encrypted by **Firebase Authentication** (never stored in plain text)
- **Google Single Sign-On (SSO)** support
- **Role-based access control** — Manager vs Shop Assistant
- New users default to **Shop Assistant** role
- Manager access granted manually via Firebase Console
- All Firestore communication over **HTTPS**
- **Password reset** via Firebase email link

---

## 🚀 Getting Started

### Prerequisites
- Android Studio (Hedgehog or later)
- JDK 17
- Android device or emulator (API 24+)
- Firebase project (see setup below)

### Installation

**1. Clone the repository**
```bash
git clone https://github.com/Lefakatleho14/InventoryPro-Android.git
cd InventoryPro-Android
```

**2. Add google-services.json**

This file is not committed for security reasons. To get it:
- Go to [Firebase Console](https://console.firebase.google.com)
- Open the **InventoryPro** project
- Project Settings → Download `google-services.json`
- Place it in the `app/` folder

**3. Build and run**

Open in Android Studio → Run on device or emulator


---

## 👥 Demo Credentials

| Role | Email | Password |
|---|---|---|
| Manager | lefa@inventorypro.com | Manager@123 |
| Shop Assistant | kago@inventorypro.com | Shop123 |

---

## 🧪 Unit Tests

The project includes **50 unit tests** across 4 test classes:

| Test Class | Tests | What It Tests |
|---|:---:|---|
| `ProductModelTest` | 12 | isLowStock, stockValue, formatted price |
| `SaleModelTest` | 12 | formattedTotal, formattedUnitPrice, data |
| `UserModelTest` | 11 | isManager(), role constants, user data |
| `InventoryCalculationTest` | 15 | Total value, stock deduction, filters |

**Run all tests:**
```bash
./gradlew test
```

**Results:**

50 tests completed — 50 passed ✅ — 0 failed


---

## ⚙️ CI/CD — GitHub Actions

Every push to `main`/`master` automatically:

1. **Runs all 50 unit tests**
2. **Builds the debug APK**
3. **Uploads test results** as artifacts
4. **Uploads the APK** as a downloadable artifact

Workflow file: `.github/workflows/android.yml`

[![Android CI](https://github.com/Lefakatleho14/InventoryPro-Android/actions/workflows/android.yml/badge.svg)](https://github.com/Lefakatleho14/InventoryPro-Android/actions)

---

## 📦 NuGet / Gradle Dependencies

```groovy
// Firebase
implementation platform('com.google.firebase:firebase-bom:32.7.2')
implementation 'com.google.firebase:firebase-auth-ktx'
implementation 'com.google.firebase:firebase-firestore-ktx'

// Google Sign-In
implementation 'com.google.android.gms:play-services-auth:21.0.0'

// Material Design 3
implementation 'com.google.android.material:material:1.11.0'

// Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'

// Glide
implementation 'com.github.bumptech.glide:glide:4.16.0'

// Testing
testImplementation 'junit:junit:4.13.2'
testImplementation 'org.mockito:mockito-core:5.8.0'
```

---

## 👨‍💻 Team — CodeCrafters

| Member | Role | Responsibilities |
|---|---|---|
| **Lefa Mokoena** | Project Manager | Architecture, auth, coordination |
| **Bokang Dlamini** | Backend Developer | Firebase, repository layer, CI/CD |
| **Kago Khumalo** | Frontend Developer | UI layouts, Material Design |
| **Mabizela Nkosi** | QA Tester & Analyst | Testing, requirements, docs |

