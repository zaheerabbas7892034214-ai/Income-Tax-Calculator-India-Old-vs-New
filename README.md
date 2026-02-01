# Income Tax Calculator (India) - Old vs New Regime

An Android application for calculating income tax under both the Old and New tax regimes in India. The app helps users compare tax liabilities between the two regimes and choose the most beneficial option.

## Features

### Free Features
- Calculate income tax for Old and New regimes
- Compare tax liabilities side-by-side
- Support for multiple Financial Years (FY 2023-24, 2024-25, 2025-26)
- Detailed breakdown of tax calculations
- Recommendation for optimal regime

### Pro Features (₹199 one-time purchase)
- Export PDF reports with detailed tax summary
- Save multiple user profiles
- Year-wise comparison
- Restore purchase functionality

## Technical Specifications

- **Package Name**: com.yourcompany.incometax
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI Framework**: Jetpack Compose with Material 3
- **Database**: Room
- **Billing**: Google Play Billing Library 6+

## Project Structure

```
app/src/main/java/com/yourcompany/incometax/
├── data/
│   ├── dao/
│   │   ├── EntitlementDao.kt
│   │   └── ProfileDao.kt
│   ├── database/
│   │   └── TaxDatabase.kt
│   ├── entity/
│   │   ├── EntitlementEntity.kt
│   │   └── ProfileEntity.kt
│   └── model/
│       ├── TaxInputs.kt
│       ├── TaxResult.kt
│       └── TaxSlab.kt
├── domain/
│   ├── billing/
│   │   └── BillingManager.kt
│   ├── calculator/
│   │   └── TaxCalculator.kt
│   ├── export/
│   │   └── PdfExporter.kt
│   └── repository/
│       ├── EntitlementRepository.kt
│       └── ProfileRepository.kt
├── ui/
│   ├── navigation/
│   │   └── NavGraph.kt
│   ├── screens/
│   │   ├── DeductionsScreen.kt
│   │   ├── DisclaimerScreen.kt
│   │   ├── ExportScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── IncomeInputsScreen.kt
│   │   ├── PaywallScreen.kt
│   │   ├── ProfilesScreen.kt
│   │   ├── ResultScreen.kt
│   │   ├── SettingsScreen.kt
│   │   └── SplashScreen.kt
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── viewmodel/
│       ├── BillingViewModel.kt
│       ├── DeductionViewModel.kt
│       ├── HomeViewModel.kt
│       ├── IncomeViewModel.kt
│       ├── ProfileViewModel.kt
│       ├── ResultViewModel.kt
│       └── ViewModelFactory.kt
├── MainActivity.kt
└── TaxCalculatorApp.kt
```

## Tax Calculation Logic

### Old Regime
- Includes deductions:
  - Section 80C (up to ₹1.5 lakh)
  - Section 80D (health insurance)
  - HRA exemption (basic calculation)
  - Standard deduction (₹50,000)

### New Regime
- No deductions except standard deduction
- Standard deduction varies by Financial Year:
  - FY 2023-24: No standard deduction
  - FY 2024-25: ₹50,000
  - FY 2025-26: ₹75,000

### Tax Slabs
The app includes accurate tax slabs for both regimes across multiple financial years with 4% Health and Education cess applied.

## Building the Project

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34
- Gradle 8.2

### Build Instructions

1. Clone the repository
2. Open the project in Android Studio
3. Sync the project with Gradle files
4. Build and run on an emulator or physical device

```bash
./gradlew assembleDebug
```

## Screens

1. **Splash Screen** - Initial loading screen
2. **Home Screen** - Select Financial Year and start calculation
3. **Income Inputs Screen** - Enter salary components and other income
4. **Deductions Screen** - Enter deductions for Old Regime
5. **Result Screen** - Compare tax calculations with recommendations
6. **Profiles Screen** - View and manage saved profiles (Pro)
7. **Export Screen** - Export PDF reports (Pro)
8. **Paywall Screen** - Upgrade to Pro features
9. **Settings Screen** - App settings and purchase restoration

## In-App Purchase

- **Product ID**: `tax_pro_unlock`
- **Price**: ₹199 (one-time purchase)
- Includes purchase restoration and proper handling of pending transactions

## Disclaimer

This app provides tax calculations for informational purposes only. It is not a substitute for professional tax advice. Please consult a qualified tax professional for legal advice and accurate tax filing.

## License

Copyright © 2024. All rights reserved.
