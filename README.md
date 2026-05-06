<p align="center"><img src="./src/main/resources/icon.png" alt="Mod icon" width="128"></p>

<h1 align="center">Pathed <br>
    <a href="https://github.com/junyali/pathed/actions/workflows/build.yml"><img alt="GitHub Actions Workflow Status" src="https://img.shields.io/github/actions/workflow/status/junyali/pathed/build.yml?branch=main"></a>
    <a href="https://github.com/junyali/pathed/releases/"><img alt="NeoForge 1.21.1" src="https://img.shields.io/badge/NeoForge-1.21.1-blue?logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAMAAAD04JH5AAAAxlBMVEUAAAB0dICJjZMYGyFSNCBYVVZlZGZyXFV6UER6fIaCVkSCeHSCg42FfHiFh4+KgXyPhoKiqaulbl6nrrKpkoqrVDWrdWWueWmvtreyuruzWjK3v7+5fGm9gWS+ycm/dWC/hWjBh3jDzs3FZjHJ09PKbjTK1dPPs7/P2trY5OPafDDalWTioXLs498SFBklKTJlUkx0dH+KjZOMcGefpKijTTapr7K8xsa+YDPGorjG0NDU397Wcy7lizbl8fDm2NL9+/v///97b+YiAAAALnRSTlMAgIDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAhlBwPgAAAAFiS0dEQYnebE4AAAKLSURBVHja7ZvbUlNBEEWD4o0oSlAUVFS8ogJBOScq8fL/X+XDXvOwp6amToLEoarXG53uzpqp6jkXKqNR0AjXVk4m8GXlhEAINCPAUGwZ2wPYuiBpGvE5N34N4PyCpI0IgRBoReBQPBVvxAfngfFOHIj7xkERmvNXJsCyejETP5ypQZBU/2xWxJuHQAi0JsDg7Qpy9uGeuCVcgBSCpIxFJvBElAV8zVkl3b8KF/Dd8ZRZjRAIgdYEuETtG0zTmOF6KOj+2CBIik/juDiN5fsB34ipr+u3INgZBEnxjZgWz6MQCIEQKJ8Dfh2+DYz1e0HwkUGQFApS/ZBzoHhYZcv7Lgh+MwiSkm3SAkdxCIRAowIb4oXYE28NgqRQUBZYYgx9sXPx0yBY3JZuyEEUAiHQpkC61L02agKemeqXFsjW7BQFnFQfAiFwBQWeizROn8Un4QIfBUFSKEj1tFtAIFv5H8E9twv4mkmhINuJEAiBRgXuiOoY0u+moKvPH5BCQap/KXjiRIAvHp2K6kFEvxMxr0AKBaneX6ghwBeHQAi0JsBz245IY3Qkrovi/SdBUihIH/I6l3/HVAX4kNvp7Hg5FsU7cIKk+JE1p11vhEAItC0wEbx32eNWc13UxpAUClI97RYQ8I3o5kvSFZceAiFwFQV84njwWzeyp0HxzwT8zOFif2xkdwD+kioEQiAElhbgMjrx/1HyXTcMgp45KV6HFxDId0L4w7o/kHtmXyMEQqA1AZ5KN0VfHkfxSjwzCHpm1sabZwIerap3FYasOQRCoG2Bu6LaZ1KhWujNywJnor8UvHkIhEAzAmuCKxWTsnkpeHO+OP3k7HTlZL+2C4EQ+O8CaysnfmzeCn8BujwDqaYp4wAAAAAASUVORK5CYII="></a>
    <a href="https://github.com/junyali/pathed/blob/main/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/junyali/pathed"></a>
    <br>
    <a href="https://ko-fi.com/junyali"><img alt="kofi" height="28" src="https://img.shields.io/badge/Kofi-F16061.svg?logo=ko-fi&logoColor=white"></a>
    <br><br>
</h1>

**Quick Disclaimer: this mod is super work-in-progress. As of writing this it is NOT yet complete, though alpha builds are available!**

Pathed is a Minecraft progression mod aimed to add a twist on your vanilla journey. Choose a **path** at the start of your journey and become locked to a single **tool**. That tool becomes your lifeline - it can do everything, but does well with specific asks aligned with your path.

## Gallery

**Select a Path**

![Choose your Path](./demo/choose_path.png)

**Progress in your journey**

![Progression](./demo/progression.png)

**Master your tool**

![Attribute](./demo/attribute.png)

## Features
- This mod has 5 unique paths covering different gameplay mechanics (TBA). If you prefer the vanilla experience, the human option is always available!
- You are locked to your tool (TBA). Combat, mining, harvesting - all in one tool no matter your path.
- Attribute System to manage your path's attributes. Unlock more attributes through the progression system.
- Custom progression system (kinda inspired by vanilla Minecraft's advancement system and Payday 2's skill trees)

## How to run / build

### Requirements

**NeoForge:** This mod was built on NeoForge version 21.1.224 for 1.21.1

### Running

Download the latest release [here](https://github.com/junyali/pathed) or from your favourite mod distribution platform (TBA).

### Building

This mod was developed on IntelliJ IDEA Ultimate, though may work with other Java IDEs that support the Gradle Build Tool.
Importing from `build.gradle`, run:

```console
$ ./gradlew
```

Then to launch the client configuration, run:

```console
$ ./gradlew runClient
```

Be sure to sync all gradle projects and refresh dependencies if you encounter issues related to Gradle.

