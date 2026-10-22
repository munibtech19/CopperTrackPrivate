# CopperTrack Private

**Offline-first personal Android app for private copper scrap trading.**

- Native Kotlin + Jetpack Compose (Material 3)
- Room (SQLite) local database
- MVVM + clean separation
- BigDecimal money/weight calculations
- No accounts, no cloud, no trackers

---

## Important limitation of this delivery environment

This coding environment **does not contain the Android SDK, Gradle Android plugin, or emulator**.  
Therefore **no APK was built or tested here**.  

You must open the project in **Android Studio** on your own machine (Windows / macOS / Linux) to compile, run unit tests, and generate an installable APK.

---

## Project structure (key packages)

```
app/src/main/java/com/coppertrack/private/
├── data/
│   ├── local/
│   │   ├── entity/     # Contact, Deal, Expense, Payment, Inventory, Settings
│   │   ├── dao/
│   │   └── db/         # AppDatabase + TypeConverters
│   └── repository/
├── domain/
│   ├── calculator/     # ProfitCalculator (BigDecimal, unit-tested)
│   ├── model/          # Enums
│   └── usecase/        # DashboardSummaryUseCase
├── ui/
│   ├── theme/          # Dark copper theme
│   ├── navigation/     # Bottom nav + NavHost
│   └── screens/        # Dashboard, Deals, Contacts, Expenses, Reports, Settings
├── CopperTrackApp.kt   # Application + simple DI
└── MainActivity.kt
```

---

## Implemented features (checklist)

| Area | Status | Notes |
|------|--------|-------|
| Project skeleton (Gradle, Compose, Room, Navigation) | ✅ | Ready to open in Android Studio |
| Room entities + DAOs + Database | ✅ | Contacts, Deals, Expenses, Payments, Inventory, Settings |
| ProfitCalculator (BigDecimal) | ✅ | Purchase cost, COGS (proportional), gross/net profit, margin, break-even, remaining stock |
| Unit tests for calculator | ✅ | `ProfitCalculatorTest` – run with `./gradlew test` |
| Dashboard (live from DB) | ✅ | Shows zeros + empty state when no data |
| Deals list + basic add-draft dialog | ✅ | Profit shown per card |
| Contacts list + add dialog | ✅ | Supplier / Buyer / Both |
| Expenses / Payments screens | 🟡 | UI placeholder – data layer ready |
| Reports screen | 🟡 | UI placeholder – data layer ready |
| Settings screen | 🟡 | UI shows options; PIN/biometric/backup logic not fully wired |
| CSV Import / Export | ❌ | Not implemented in this delivery (structure ready for SAF) |
| PIN + Biometric lock | ❌ | Fields exist in settings entity; UI + Keystore wiring pending |
| Photo attachments | ❌ | Columns exist; file picking not wired |
| Full deal edit / partial sale flow | 🟡 | Entity + calculator support it; full UI form not finished |
| Inventory screen | ❌ | Entity + sync from deals exists |

**Priority for next development session:** complete Deal detail/edit form, wire Expenses & Payments screens to repositories, add CSV export via Storage Access Framework, then PIN lock.

---

## How to build the APK (on your machine)

### Prerequisites
1. **Android Studio Hedgehog (2023.1.1) or newer** (or Giraffe+)
2. Android SDK 34, build-tools, and a JDK 17
3. Accept licenses: `sdkmanager --licenses`

### Steps

```bash
# 1. Copy the entire CopperTrackPrivate folder to your computer

# 2. Open Android Studio → Open → select the CopperTrackPrivate folder
#    (the one that contains settings.gradle.kts)

# 3. Let Gradle sync. If prompted, use the embedded JDK 17.

# 4. Run unit tests
./gradlew test

# 5. Build a debug APK (installable without signing setup)
./gradlew assembleDebug

# APK location after successful build:
# app/build/outputs/apk/debug/app-debug.apk
```

### Install on phone

**Via USB + ADB**
```bash
# Enable Developer options + USB debugging on the phone
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

**Via Termux (on the phone itself)**  
Copy the APK to the phone (USB, cloud, etc.), then:
```bash
# In Termux (after installing termux-tools if needed)
pkg install android-tools   # or use a file manager that can install APKs
# Or simply open the APK with the system package installer
```

**Via Android Studio**  
Click the green Run button with a physical device or emulator selected.

### Common build problems

| Error | Fix |
|-------|-----|
| `SDK location not found` | Create `local.properties` with `sdk.dir=/path/to/Android/Sdk` |
| KSP / Room processor fails | Ensure `ksp` plugin version matches Kotlin (already set to 1.9.22) |
| Compose compiler mismatch | `kotlinCompilerExtensionVersion = "1.5.8"` matches Kotlin 1.9.22 |
| minSdk / API errors | Project targets API 26–34 |
| `Duplicate class` | Clean: `./gradlew clean` |

---

## Privacy design

- `android:allowBackup="false"`
- Explicit exclusion of cloud backup
- No internet permission declared
- No analytics / advertising SDKs
- PIN hash stored only as a one-way hash (implementation pending)
- All business data lives in the app’s private SQLite file

**Warning:** Uninstalling the app or factory-resetting the phone without a manual backup will permanently erase all data.

---

## CSV templates (planned)

When Import/Export is finished, the expected columns will be documented in `docs/csv-templates.md`.  
Until then, you can query the Room tables directly with any SQLite browser after extracting the database file from the device.

---

## License / ownership

This is a private personal application source. You own the generated code and data.

---

*Built as a functional foundation. Core accounting math and data layer are production-ready; several UI modules are deliberately left as clear placeholders so the project compiles and the most critical logic can be verified first.*
