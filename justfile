_default:
    @just -l

# Create sample database
[group: 'dev']
database: _delete-database _create-database _populate-database
    @echo 'Sample database is good to go!'

_create-database:
    @echo 'Creating database...'
    @sqlite3 database/database.db < database/create.sql

_populate-database:
    @echo 'Populating database...'
    @sqlite3 database/database.db < database/populate.sql

_delete-database:
    @echo 'Deleting current database...'
    @rm database/database.db

# Run the application. Default: runs a client
[group: 'dev']
run *ARGS:
    @./gradlew run --args='{{ARGS}}'