# Smart Pantry Manager

A Java Android app (Mobile App Development 700) that reduces food waste by tracking the ingredients you
already have and suggesting recipes you can cook using **strictly** those ingredients.

## Features
- Pantry management: add, edit, delete items (name, quantity, unit, optional expiry date) with input validation
- Pantry list (RecyclerView + custom adapter bound to the database), expiring-soon highlight
- 20 recipes seeded on first run
- Suggested Recipes screen using the strict-matching rule, with an empty-state message
- Recipe Detail screen (ingredients + method), Settings screen, bottom navigation
- No maps, GPS or location features

## Database choice: SQLite via Room
Room gives compile-time checked SQL, simple DAOs for full CRUD, and fully local, offline storage with no
server or account to set up. Data persists after the app is closed and reopened.

Tables: `pantry_items`, `recipes`, `recipe_ingredients` (FK -> recipes, cascade delete).

## Strict-matching rule
A recipe is suggested only if **every** ingredient exists in the pantry in at least the required quantity
(`logic/IngredientMatcher.java`). Names are normalised (case, whitespace, plural/singular such as
tomato/tomatoes) and quantities converted to base units (g, ml, count; kg/l/tsp/tbsp/cup supported).
Mass, volume and count are never mixed, so 1 tbsp butter does not satisfy 10 g butter.

## Setup / run
1. Install Android Studio (Hedgehog or newer) with Android SDK 34.
2. File > Open > select the `SmartPantryManager` folder; let Gradle sync (it will fetch the wrapper).
3. Run on an emulator or device (API 24+).
4. Unit tests: right-click `app/src/test/.../IngredientMatcherTest` > Run.

## Structure
- `data/` Room entities, DAOs, database, seeder
- `logic/` IngredientMatcher (strict matching)
- `ui/` Activities and adapters
- `res/` layouts, menu, values
