## 📌 **Part 2: UI Testing**

### **Test the DemoQA Website**

URL: https://demoqa.com  
This is a stable demo site specifically designed for automation practice.

### **Test These Sections:**

#### **Option A: Test Form Submission:** [https://demoqa.com/automation-practice-form](https://demoqa.com/automation-practice-form)

- Fill the student registration form  
- Upload a file  
- Select a date from the date picker  
- Choose from dropdowns  
- Submit and verify the success modal

**Option B: Test Web Tables:** [https://demoqa.com/webtables](https://demoqa.com/webtables)

- Add a new record  
- Edit existing record  
- Delete record  
- Search functionality  
- Sorting validation

### **UI Test Requirements:**

- Use **Playwright**  
- Implement Page Object Model  
- Handle dynamic waits properly  
- Take screenshots of failures

### **Core Tech Stack:**

```xml
<!-- Required Dependencies -->
- JUnit 5 (latest)
- REST Assured (API testing)
- Playwright for Java (UI testing)
- AssertJ (assertions)
- Jackson (JSON handling)

<!-- Bonus -->
- Allure (reporting)
- Lombok (reduce boilerplate)
```

## **✅ Deliverables:**

### **1\. GitHub Repository with:**

- Working test suite
- Clear package structure  
- .gitignore file  
- pom.xml with all dependencies

### **2\. README.md including:**

```
# QA Automation Test Suite

## Prerequisites
- Java 11+
- Maven 3.6+
- Chrome browser

## How to Run
# Run all tests
mvn clean test

# Run only API tests
mvn test -Dgroups="api"

# Run only UI tests
mvn test -Dgroups="ui"

## Test Strategy
[Explain your approach, what you prioritized and why]

## Challenges & Solutions
[Any issues you encountered and how you solved them]

## What I Would Add With More Time
[Additional tests or improvements]
```

### **3\. Test Report:**

- Screenshot of test execution  
- OR Surefire HTML report  
- OR Allure report (bonus)

## 💡 **What We're Looking For (quality over quantity):**

1. **Test Design (20%)**  
   - Meaningful test scenarios  
   - Good mix of positive/negative tests  
   - Clear test names and structure

2. **Code Quality (30%)**  
   - Clean, maintainable code  
   - Proper use of Page Object Model  
   - DRY principle (no duplication)  
   - Good assertions

3. **Framework Architecture (40%)**  
   - Logical project structure  
   - Reusable components  
   - Configuration management  
   - Base classes and helpers

4. **Documentation (10%)**  
   - Clear README  
   - Code comments where needed  
   - Meaningful commit messages

## **Minimum Requirements:**

Must have:
- At least 2 UI tests with Page Object Model  
- Basic assertions using AssertJ  
- README with setup instructions

Nice to have:
- Data-driven tests  
- Custom waits/retry logic  
- Allure reporting  
- Parallel execution  

## **Important Notes:**

- These are PUBLIC test services \- be respectful, don't overload them  
- Restful Booker resets data periodically, that's normal  
- Focus on demonstrating best practices over test quantity  
- If a service is temporarily down, document it and mock the scenario  
- Commit frequently to show your development process

## **Getting Started:**

```shell
# Quick start
mvn archetype:generate -DgroupId=com.flamingo.qa \
  -DartifactId=qa-automation-assignment \
  -DarchetypeArtifactId=maven-archetype-quickstart \
  -DinteractiveMode=false

cd qa-automation-assignment

# Add dependencies to pom.xml
# Start with API tests first
# Then move to UI tests
```