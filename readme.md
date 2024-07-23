# DocGPT Plugin

![DocGPT Logo](https://plugins.jetbrains.com/files/21934/screenshot_9da436fb-83a4-4dc3-b74b-b97398f25ecc)

DocGPT is an IntelliJ plugin that harnesses the power of the LLMs to generate documentation for your code. Currently, it supports Java, Kotlin, and Dart.

## Features

- Automatically generate documentation comments for functions.
- Supports Java, Kotlin, and Dart languages.
- Easy integration with IntelliJ IDEA.
- Switch between Google Gemini and OpenAI for generating documentation.

## Installation

1. Download and install the plugin from the JetBrains Marketplace.
2. Navigate to `Settings` > `Tools` > `DocGPT` to add your API key.

## Usage

1. Open a file in Java, Kotlin, or Dart.
2. Select the function name with the cursor.
3. Press `Alt + Enter`.
4. Choose `Generate doc comment` from the menu.

## Settings

You can now switch between Google Gemini and OpenAI as the provider for generating documentation.

1. Navigate to `Settings` > `Tools` > `DocGPT`.
2. Use the dropdown menu to select either `Google Gemini` or `OpenAI`.
3. Enter the respective *API key, model and the maximum amount of tokens* for the selected provider.
4. Save your settings.

![Settings](https://plugins.jetbrains.com/files/21934/screenshot_dc5065f1-0c3a-4a60-967b-05847cf33f11)

## Screenshots

### Generated a Doc Comment

![Generating Doc Comment](https://plugins.jetbrains.com/files/21934/screenshot_2ea9bdc4-6d00-4ab3-98b6-f452bb55f6a1)

### Settings

![Settings](https://plugins.jetbrains.com/files/21934/screenshot_922abece-4cb3-486a-9ca2-6a5d135e760b)

## Important Notes

- Currently, the plugin only generates doc comments for functions.
- The plugin is still under development and has not been extensively tested yet.
- I welcome feedback and suggestions to improve the functionality and usability of this plugin.
- Use at your own risk.

## Features planned for the future
- Supporting more LLMs
- Setting the function context scope

## Contributing

We welcome contributions to enhance the DocGPT plugin. Please open an issue or submit a pull request on the [GitHub repository](#).

## Feedback

For feedback, questions, or suggestions, please create an issue on the [GitHub repository](#).
