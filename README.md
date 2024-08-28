![Enhanced Surveillance](images/title.png)
# Enhanced Surveillance
Enhanced Surveillance is a Minecraft plugin that logs all player activities for detailed and organized record-keeping. It stores player data in a database and .yml files named after the player's UUID, providing a reliable way to monitor player actions and keep your server secure.

## Getting Started

- [Configuration](#configuration)
- [Commands](#commands)
- [Support](#support)

## Configuration

To configure the Enhanced Surveillance plugin, edit the `config.yml` file located in the plugin's directory. To configure each event listener, edit the `bukkit-events.yml` and `paper-events.yml` located in `events/player` directory.
### `config.yml`
- ### Database Configuration

  This section configures the database connection parameters used by the plugin to store and retrieve data.

    - **dialect** - Specifies the database dialect. For example, ***MySQL8Dialect*** for MySQL.
    - **address** - The IP address or hostname of the database server.
    - **port** - The port on which the database server is listening.
    - **database** - The name of the database.
    - **username** - The username used to connect to the database.
    - **password** - The password used to connect to the database.
      <br></br>
    ```yml
    database:
        dialect:    MySQL8Dialect
        address:    localhost
        port:       3306
        database:   enhanced_surveillance
        username:   root
        password:   ""
    ```

- ### Hibernate Configuration

  This section contains Hibernate-specific settings.

    - **show_sql** - If set to true, SQL statements will be logged to the console.
    - **format_sql** - If set to true, SQL statements will be formatted.
    - **sql_comments** - If set to true, SQL comments will be included in the logged SQL statements.
      <br></br>
    ```yml
    hibernate:
        show_sql:       false
        format_sql:     true
        sql_comments:   true
    ```

- ### MemoryService Configuration

  This section contains asynchronous service-specific settings.

    - **services** - Amount of independent services.
    - **threads** - Amount of threads per service.
      <br></br>
    ```yml
    memory-service:     # 5 (services) * 3 (threads) = 15 (threads usage) 
        services:       3
        threads:        1
    ```

<br></br>
### `bukkit/paper-events.yml`
> [!WARNING]
> It is highly recommended not to use levels higher than 0 or 1 unless absolutely necessary, such as for debugging or when there is a critical need to capture all details of an event.

> [!CAUTION]
> As the record level increases, the size of each event also grows significantly—potentially up to 500 bytes at the highest levels in some Events. Given that events can be generated in large numbers every second, this can quickly lead to excessive database storage consumption, risking the exhaustion of available disk space.

- ### Event listener

  This is example of section inside `bukkit-events.yml` and `paper-events.yml`, each listener will contain at least 2 parameters.

    - **enabled** - If set to true, EventListener will be active.
    - **level** - Specifies level of information to record.
      <br></br>
    ```yml
    PlayerInteractEntityEvent:
        enabled:                    false
        level:                      1           # Levels:   0 / 1 / 2 / 3
    ```

- ### Event listener (with additional parameters)

  This is example of section inside `bukkit-events.yml` and `paper-events.yml`, this listener contain more than 2 parameters. However in the comment behind you can read it's purpose.

    - **enabled** - If set to true, EventListener will be active.
    - **level** - Specifies level of information to record.
    - **distance** - Specifies distance the player have to reach from last position to record new Event.
      <br></br>
    ```yml
    PlayerMoveEvent:
        enabled:                    true
        level:                      3           # Levels:   0 / 1 / 2 / 3
        distance:                   15          # Not-Recommended lower numbers than 15
    ```
---

## Support

For questions about Enhanced Surveillance, first try the ~~[Wiki](https://example.com/)~~ to see if your question is already answered there.
If you can't find what you're looking for, contact me via Discord ***.n.u*** or ***❤ Miciasty ❤***

### Work in progress...
(╯°□°)╯︵ ┻━┻