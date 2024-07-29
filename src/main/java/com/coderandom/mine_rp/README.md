# MineRP Plugin

## Commands

### Balance Commands

- **`/balance`**
    - **Usage:** `/balance` or `/balance <player_name>`
    - **Description:** Allows a player to check their balance.
    - **Permissions:**
        - `mine_rp.economy.balance`: Allows checking own balance.
        - `mine_rp.economy.balance.others`: Allows checking another player's balance.

### Payment Commands

- **`/pay`**
    - **Usage:** `/pay <player_name> <amount>`
    - **Description:** Allows a player to pay another player.
    - **Permissions:**
        - `mine_rp.economy.pay`: Allows paying another player.

## Permissions

- **Job Permissions**
    - **`mine_rp.job.<job_key>`**: A player will have this permission when they have a specific job.
    - **`mine_rp.job.category.<job_category>`**: A player will have this permission when they have a job within a
      specific category.
