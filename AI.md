# AI Use Log

Throughout this project, I use Microsoft Copilot (built in to VSCode) with the Claude Sonnet 4 model to help me with
managing code quality and project structure design.

## 1. 18 September 2025: AI Usage Documentation Setup

### Prompt: I have created AI.md to note down my AI Usage in markdown.

From now on, including this prompt, modify AI.md for each signifcant prompt I give you (I will specify which) in the following format:

\## [X]. 18 September 2025: [KEY CHANGE]

\### Prompt: [my prompt]

\### Improvements made:

\[Improvements here]

\### Files modified:

\[Files modified]

### Improvements made:

- Established AI usage documentation format for tracking significant changes
- Created structured logging template with date, key change summary, prompt details, improvements, and modified files

### Files modified:

- AI.md (created initial documentation structure)

## 2. 18 September 2025: Package Structure Reorganization (Corrected)

### Prompt: Redo the movement, there were some broken imports when ran on IntelliJ with gradle and after reverting, there are some duplicate files

### Improvements made:

- Successfully cleaned up duplicate files from previous failed reorganization attempt
- Maintained the logical package structure: `tasks`, `data`, `command`, `ui`, `exceptions` 
- Fixed all import statements systematically across main and test files
- Ensured proper compilation with gradle for both main and test code
- Verified that all tests pass successfully
- Resolved package dependency issues that were causing compilation failures in IntelliJ
- Package structure now works correctly for both VS Code and IntelliJ IDE environments

### Files modified:

**Import fixes applied to:**
- src/main/java/john/John.java (added imports for moved classes)
- src/main/java/john/JohnChatBot.java (added imports for moved classes)
- src/main/java/john/Main.java (added imports for moved classes)
- src/test/java/john/ParserTest.java (added imports for moved classes)
- src/test/java/john/StorageTest.java (added imports for moved classes)

**Structure maintained:**
- john.tasks package: Task.java, ToDo.java, Event.java, Deadline.java
- john.data package: TaskList.java, Storage.java  
- john.command package: Parser.java
- john.ui package: Ui.java, MainWindow.java, DialogBox.java
- john.exceptions package: JohnException.java
- john root: John.java, JohnChatBot.java, Main.java, Launcher.java