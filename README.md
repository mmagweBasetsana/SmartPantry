# Smart Pantry Manager

## Overview

Smart Pantry Manager is an Android mobile application developed using Java and Android Studio to help users manage their household pantry efficiently. The application allows users to keep track of available ingredients, monitor expiry dates, and discover recipes that can be prepared using ingredients they already have.

The main goal of the application is to encourage better food management, reduce food waste, and help users make effective use of their existing food supplies.

## Features

- **Pantry Management:** Add, view, update, and delete pantry items.
- **Ingredient Tracking:** Record ingredient names, quantities, and units of measurement.
- **Expiry Date Monitoring:** Store expiry dates and identify ingredients that are approaching expiry.
- **Expiry Notifications:** Display notification or pop-up messages to remind users about items that need attention.
- **Recipe Recommendations:** View recipes based on ingredients available in the pantry.
- **Strict Ingredient Matching:** Recommend a recipe only when all its required ingredients are available.
- **Persistent Data Storage:** Store pantry items and recipe information using an SQLite database.
- **User-Friendly Interface:** Provide accessible screens and navigation for managing pantry information and exploring recipes.

## Technologies Used

- **Programming Language:** Java
- **Development Environment:** Android Studio
- **User Interface:** XML layouts
- **Database:** SQLite
- **Platform:** Android
- **Version Control:** Git and GitHub

## How It Works

1. Open the application on your Android device.
2. Add ingredients to your pantry, including their quantities and units.
3. Record expiry dates where applicable.
4. View and manage your stored pantry items.
5. Browse the available recipe recommendations.
6. Select recipes that match the ingredients currently available in your pantry.
7. Use expiry reminders to help prioritise ingredients that should be used soon.

The recipe recommendation feature follows a strict matching rule: all ingredients required by a recipe must be available in the pantry before that recipe is recommended.

## Database

The application uses an SQLite database to store and retrieve information locally on the device.

The main database tables are:

- **Pantry:** Stores ingredient names, quantities, units, and expiry dates.
- **Recipes:** Stores recipe names and preparation instructions.
- **Recipe Ingredients:** Stores the ingredients and quantities required for each recipe.

## Installation and Setup

### Requirements

- Android Studio
- Java development support
- Android device or Android emulator
- USB cable and USB debugging enabled if testing on a physical device

### Run the Project

1. Clone or download this repository to your computer.
2. Open Android Studio.
3. Select **Open** and choose the downloaded project folder.
4. Allow Gradle to synchronise and download the required dependencies.
5. Connect an Android device with USB debugging enabled, or start an emulator.
6. Select the target device and click **Run**.

## Project Objectives

The objectives of Smart Pantry Manager are to:

- Simplify household pantry management.
- Help users monitor ingredient quantities and expiry dates.
- Reduce unnecessary food waste.
- Recommend practical recipes using existing ingredients.
- Apply Android development concepts, including Java programming, XML layouts, intents, adapters, activity lifecycle management, and SQLite database operations.

