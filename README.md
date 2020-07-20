# PasswordProtectTenshi

Have you ever needed an angel to protect your minecraft server?

Well look no further, cause here we have, uh, the perfect idol angel for you.

Meet PasswordProtectTenshi, also known as PPTenshi.

She will serve as the guardian angel of your Minecraft server.

Currently supporting: `spigot-api:1.16.1-R0.1-SNAPSHOT` , Spigot 1.16.1.

**PPTenshi is a Spigot Minecraft plugin**

## Features

- Async processing.
- Fast and lightweight.
- Minimal memory footprint.
- MySQL and H2 database support.
- `/login`, `/register` and `/unregister` player commands.
- `/unregisterplayer` admin command.
- Salted PBKDF2 password hashing.
- Easy to setup. 
- Translation support, implement your own messages and prefixes.
- 
## Getting Started 

To get started download (or compile) PPTenshi and place it in your plugins folder. After starting your server, the folder `plugins/PasswordProtectTenshi` will be created with the configuration files inside. 

If you do not want to change these settings, the plugin will default to using a H2 local database with no extra configuration needed.

## Configuration Files

The folder `plugins/PasswordProtectTenshi` has the following files inside: 

- `config.yml` , which is currently used to provide database related configuration. The following options are found inside:
  -  mysql.enable: if true, the plugin will use the provided MySQL database to store passwords.
  - mysql.database_host: sets up the MySQL database domain.
  - mysql.database_port: sets up the MySQL database port.
  - mysql.database_name: sets up the MySQL database name.
  - mysql.database_user: sets up the MySQL database user.
  - mysql.database_password: sets up the MySQL database password.
  - h2.database_name: sets up the H2 database name, this will affect the created .mv.db file name.
  - h2.database_user: sets up the H2 database user.
  - h2.database_password: sets up the H2 database password.
- `language.yml` , which is used for localization purposes, therefore, all the plugin messages are stored here. Most if not all of the messages are self explanatory. The following options are the ones I believe need clarification:
  - random.use_random_login-register_messages: if true, the plugin will pick a random login / register message to show when greeting the player. If false, it will always use the first one in the list.
  - no_arguments: is used when the specified command was sent with no arguments.
- `pass.mv.db` , which is the default H2 database used to store passwords.

If using a MySQL database, you will need to have an existing database and provide its name.

After creating the H2 database, if you change the H2 database user or password the plugin will not work until the correct credentials are provided again. One alternative to fix this problem is to erase the locked database, but this will erase all of the passwords stored and create a new database.

## Compiling
**Requirements**: 
JDK 8

To compile PPTenshi run `gradlew build` , the compiled jar will be at `build/libs`