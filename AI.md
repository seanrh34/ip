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

## 3. 18 September 2025: Comprehensive Javadoc Documentation Implementation

### Prompt: Check all of the methods and classes in the repo, ensure that each and every one of them EXCEPT for getters and setters, as well as overrrides that do not change their meaning has Javadoc comments according to the following standards:

[...detailed Javadoc standards...]

### Improvements made:

- Audited all Java files for missing Javadoc documentation
- Added proper class-level Javadoc to JohnException with detailed description of purpose
- Enhanced all method Javadoc comments to follow proper format with:
  - Opening /** on separate line
  - Descriptive first sentence starting with action verbs (Returns, Constructs, etc.)
  - Proper @param descriptions with punctuation
  - Complete @return descriptions where applicable
  - Consistent English spelling and professional tone
- Fixed incomplete @return tags throughout the codebase
- Improved constructor documentation with clear parameter descriptions
- Enhanced class documentation for UI components (MainWindow, DialogBox)
- Ensured all public methods have complete documentation while respecting exemptions for getters/setters and simple overrides
- Verified all Javadoc follows the specified format standards

### Files modified:

**Javadoc improvements:**
- src/main/java/john/exceptions/JohnException.java (added class and constructor documentation)
- src/main/java/john/tasks/Task.java (improved method documentation and @return tags)
- src/main/java/john/tasks/ToDo.java (fixed typo, enhanced class and constructor docs)
- src/main/java/john/tasks/Deadline.java (improved constructor and getter documentation)
- src/main/java/john/tasks/Event.java (enhanced constructor and getter documentation)
- src/main/java/john/ui/MainWindow.java (added class documentation and setJohn method docs)
- src/main/java/john/ui/DialogBox.java (improved class, constructor, and static method documentation)

## 4. 18 September 2025: SLAP (Single Level of Abstraction Principle) Compliance Analysis

### Prompt: Scan each .java file and check if all the .java files follow SLAP

### Improvements made:

- Conducted comprehensive SLAP analysis across all Java files in the repository
- Verified method length compliance (all methods under 30 LoC limit)
- Checked nesting depth (all methods within 3-level maximum)
- Analyzed abstraction level consistency in all methods
- Confirmed proper method decomposition and single responsibility adherence
- Identified excellent SLAP compliance throughout the codebase
- Found no violations requiring code changes
- Validated that complex operations are properly broken down into focused helper methods
- Confirmed that each method maintains consistent abstraction levels

### Files modified:

- AI.md (no other files modified)

**Analysis completed on:**
- All Java files in src/main/java/john/ (no changes required - already SLAP compliant)
- Verified: Task hierarchy, Storage operations, Parser logic, UI components, Exception handling
- Result: Codebase demonstrates excellent SLAP adherence with proper method decomposition and abstraction levels