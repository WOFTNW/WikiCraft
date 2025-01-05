# WikiCraft

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

[![Java Version](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/projects/jdk/21/)

[![PaperMC Version](https://img.shields.io/badge/PaperMC-1.21.3-blue.svg)](https://papermc.io/downloads)

WikiCraft is a Minecraft plugin that integrates MediaWiki functionality directly into your server.

## Table of Contents

- [WikiCraft](#wikicraft)
  - [Table of Contents](#table-of-contents)
  - [Features](#features)
  - [Installation](#installation)
  - [Usage](#usage)
    - [Basic Commands](#basic-commands)
    - [Permissions](#permissions)
  - [Contributing](#contributing)
    - [Requirements](#requirements)
    - [Building](#building)
  - [License](#license)
  - [Support](#support)

## Features

- Search MediaWiki pages directly from Minecraft
- Verify edits with Minecraft accounts
- Basic page viewing functionality
- Configurable search result limits

## Installation

1. **Create Bot Account**:
   - Log in to your MediaWiki instance as an administrator
   - Go to [Special:CreateAccount](https://yourwiki.com/Special:CreateAccount) (https://yourwiki.com/Special:CreateAccount)
   - Create a new account with:
     - Username: `WikiCraftBot` (recommended)
     - Password: [secure password]
     - Email: [any valid email]
     - Real Name: `WikiCraftBot` (optional)
     - Reason: `WikiCraftBot initial creation` (optional)

2. **Configure Bot Permissions**:
   - Visit [Special:UserRights](https://yourwiki.com/Special:UserRights/WikiCraftBot) (https://yourwiki.com/Special:UserRights/WikiCraftBot)
   - Grant the bot account:
     - `bot` rights
     - `administrator` rights
   - Save changes

3. **Verify Email**:
   - Visit [Special:ConfirmEmail](https://yourwiki.com/Special:ConfirmEmail) (https://yourwiki.com/Special:ConfirmEmail)
   - Follow the email verification instructions

4. **Install Plugin**:
   - Place WikiCraft.jar in your server's plugins folder
   - Edit config.yml:
     - Set `wiki-url` to your wiki's base URL
     - Set `wiki-bot-username` to your bot's username
     - Set `wiki-bot-password` to your bot's password
   - Restart the server

## Usage

### Basic Commands

- `/wiki search [query]` - Search the wiki
- `/wiki account link` - Link your Minecraft account to wiki account
- `/wiki help` - Show command help

### Permissions

- `wikicraft.anonymous` - Basic user permissions (default)
- `wikicraft.user` - Basic user permissions (default)
- `wikicraft.admin` - Administrative permissions

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For issues or feature requests, please open an issue on my [GitHub repository](https://github.com/iHerongh/WikiCraft), or DM me on Discord at `iheron`.

If you would like to offer monetary, consider seeing my personal [Ko-fi page](https://ko-fi.com/iheron), or supporting my work directly on the [WOFTNW Patreon](https://www.patreon.com/woftnw).
