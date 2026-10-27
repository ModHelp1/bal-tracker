# demo16's bal tracker — Fabric 1.21.11

Client-side source project. Shows profit/loss since the first detected `/bal` message or your last reset, green for positive and red for negative. The HUD can be dragged and resized; colors can be cycled.

## Keys
- **O**: HUD settings (drag the preview, adjust size and colors, reset)
- **P**: reset profit baseline

## Balance updates
Type `/bal` to get a precise balance update. **The mod does not automatically query the server** and cannot promise zero-delay tracking. The scoreboard is abbreviated, so it is not used for precise balance changes. The starting baseline persists between launches. Reset only after `/bal` has been received.

## Build
Install JDK 21 and Gradle. Run `gradle build` in this folder with internet access to download Minecraft/Fabric dependencies. The output should be in `build/libs/`. This project has **not been compiled or tested against Minecraft** in this environment; dependency coordinates and API compatibility may require adjustments. Install the matching Fabric loader and Fabric API in your Minecraft client.
