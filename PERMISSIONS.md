# SkinShifter Permission System

SkinShifter supports permission plugins like LuckPerms and PermissionsEx for both Fabric and NeoForge platforms. This
document outlines all available permission nodes and their functionality.

## Platform Support

- **NeoForge**: Uses built-in PermissionAPI (works automatically)
- **Fabric**: Uses Fabric Permissions API (**requires separate mod installation**)

### ⚠️ **IMPORTANT FOR FABRIC SERVERS**

**Fabric servers MUST install the [Fabric Permissions API](https://modrinth.com/mod/fabric-permissions-api) mod
separately!**

- Download from: https://modrinth.com/mod/fabric-permissions-api
- Place `fabric-permissions-api.jar` in your server's `mods/` folder
- **Without this mod, permissions will not work on Fabric servers**
- NeoForge servers do not need this - permissions work automatically

## Implementation Status

### ✅ **Fully Working**

- **Unlock/Morph Permissions**: Players can only unlock and morph into entities they have permission for
- **Command Permissions**: Granular `.self` vs `.others` command permissions work correctly

## Quick Start

### Enable Permission System

**IMPORTANT**: Permissions are disabled by default. To enable the permission system:

1. **For Fabric servers**: Install [Fabric Permissions API](https://modrinth.com/mod/fabric-permissions-api) mod first!
2. Set `usePermissions = true` in your skinshifter config file
3. Restart your server
4. Configure permissions using your permission plugin (LuckPerms, etc.)

### For NeoForge Servers
Permissions work automatically once enabled.

### For Fabric Servers

**REQUIRED**: Install [Fabric Permissions API](https://modrinth.com/mod/fabric-permissions-api) mod first!

After installing the API mod and enabling permissions, permissions will be automatically discovered by LuckPerms when first used. No manual registration needed!

### How Permissions Work

#### When `usePermissions = true` (Permission System Enabled):

- **Unlocking**: Players can only unlock entities they have permission for
- **Morphing**: Players can only morph into entities they have permission for
- **Command Access**: `/skinshifter` commands (addShape, reset, etc.) require `.self` and `.others` permissions

#### When `usePermissions = false` (Default Behavior):

- Uses original server config behavior
- No permission checks are performed
- All players follow the same rules based on server config

### Important: Selector Permission + Granular Control

Commands use `EntityArgument.players()` which requires the `minecraft.selector.*` permission for player name targeting.
This provides proper autocomplete functionality.

**Complete Solution**: Grant both selector permission AND specific command permissions:

```bash
# REQUIRED: Grant selector permission (required for player name targeting and autocomplete)
/lp user <player> permission set minecraft.selector.* true

# Grant specific command permissions (controls what they can actually do)
/lp user <player> permission set skinshifter.command.reset.self true  # Only on themselves
# OR
/lp user <player> permission set skinshifter.command.reset.others true  # Only on others
# OR  
/lp user <player> permission set skinshifter.command.reset true  # On anyone (fallback)
```

**How it works:**

1. **`minecraft.selector.*`** - Allows using player names in commands (required by Minecraft for autocomplete)
2. **`skinshifter.command.reset.self`** - Allows clearing shapes only on themselves
3. **`skinshifter.command.reset.others`** - Allows clearing shapes only on other players
4. **`skinshifter.command.reset`** - Allows clearing shapes on anyone (fallback)

**Autocomplete**: With `minecraft.selector.*` permission, players get proper autocomplete for player names when typing
commands.

## Permission Nodes

### Commands

All `/skinshifter` commands support granular permission control with `.self` and `.others` variants:

| Permission                           | Command                 | Description                                         |
|--------------------------------------|-------------------------|-----------------------------------------------------|
| `skinshifter.command.set`            | `/skinshifter set`      | Basic permission (works on anyone)                  |
| `skinshifter.command.set.self`       | `/skinshifter set`      | Can set their own skin                              |
| `skinshifter.command.set.others`     | `/skinshifter set`      | Can set the skin of other players                   |
| `skinshifter.command.uri`            | `/skinshifter uri`      | Basic permission (works on anyone)                  |
| `skinshifter.command.uri.self`       | `/skinshifter uri`      | Can only set their own skin via an uri              |
| `skinshifter.command.uri.others`     | `/skinshifter uri`      | Can set the skin of other players via an uri        |
| `skinshifter.command.reset`          | `/skinshifter reset`    | Basic permission (works on anyone)                  |
| `skinshifter.command.reset.self`     | `/skinshifter reset`    | Can only clear their own skin                       |
| `skinshifter.command.reset.others`   | `/skinshifter reset`    | Can clear the skin of other players                 |
| `skinshifter.command.changeChatName` | `/skinshifter hasShape` | Permission to change the config option "changeName" |

## Commands

### `/skinshifter list-permissions`
Lists all available permissions with copy-paste LuckPerms commands.

**Note**: Only server operators (OP level 2+) can run this command.

## Configuration Examples

### LuckPerms Setup Examples

**Example 1: Regular Player (Self-Only)**

```bash
# REQUIRED: Grant selector permission
/lp user <player> permission set minecraft.selector.* true

# Self-only command permissions (can only use commands on themselves)
/lp user <player> permission set skinshifter.command.reset.self true
/lp user <player> permission set skinshifter.command.set.self true
```

**Example 2: Helper (Others-Only)**

```bash
# REQUIRED: Grant selector permission
/lp user <helper> permission set minecraft.selector true

# Others-only command permissions (can only use commands on other players)
/lp user <helper> permission set skinshifter.command.reset.others true
/lp user <helper> permission set skinshifter.command.set.others true
```

**Example 3: Moderator (Both Self and Others)**

```bash
# REQUIRED: Grant selector permission
/lp user <mod> permission set minecraft.selector true

# Both self and others permissions
/lp user <mod> permission set skinshifter.command.*.self true
/lp user <mod> permission set skinshifter.command.*.others true
```

**Example 4: Admin (Full Access)**

```bash
# Admin full access
/lp group admin permission set skinshifter.* true
# REQUIRED: Grant selector permission
/lp user <player> permission set minecraft.selector.* true
```

### Wildcard Permissions

| Permission                   | Description                       |
|------------------------------|-----------------------------------|
| `skinshifter.*`                | All skinshifter permissions         |
| `skinshifter.command.*`        | All command permissions (basic)   |
| `skinshifter.command.*.self`   | All self-only command permissions |
| `skinshifter.command.*.others` | All others command permissions    |

## Troubleshooting

### Debug Commands

- `/skinshifter list-permissions` - Lists all permissions with copy-paste commands

### Platform Differences

- **NeoForge**: Permissions work automatically with built-in PermissionAPI
- **Fabric**: Permissions are automatically discovered when first used (requires Fabric Permissions API mod)

## Default Behavior

If no permission plugin is installed, skinshifter falls back to operator permissions (level 2+). All permission checks
return `true` for operators and `false` for regular players.

---
*Guide by FugLong*