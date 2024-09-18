# GetOut

Entry for LibGDX Game Jam #29
Theme: The Four Elements
September 2024

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).




to do:
Done - collision detection
- pickup cards
- show cards in HUD
- activate exit
- add col det for outer wall
- recognize specific colliders to interact (cards, exit).
- smaller text button style for key menu


Done:
- key bindings menu
- main menu
- fps indicator
- settings menu


Notes:
- textures need to be power of two for teavm version
- mouse capture doesn't work on web version, user wil have to use keyboard (A/D) to turn instead of mouse.
- ESC is not handy for web. Use another key to call menu (and to cancel key binding).



This project was generated with a template including simple application launchers and a main class extending `Game` that sets the first screen.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `html`: Web platform using GWT and WebGL. Supports only Java projects.
- `teavm`: Experimental web platform using TeaVM and WebGL.
