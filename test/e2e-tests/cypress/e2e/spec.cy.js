// $npx cypress open
// $npm install --save-dev cypress-file-upload

// function to conclude a task
function concludeTask(taskName, concludeType) {
  // Wait for 1 second
  cy.wait(1000);

  // Click on the element with class '.task-bar' that has a span text of exactly (!) the taskName. Not just contains, exactly the same. check it
  cy.get('.task-bar').contains('span', taskName, { matchCase: true }).click();

  // Wait for 1 second
  cy.wait(1000);

  // Click on the button with data-tooltip concludeType
  cy.get('[data-tooltip="' + concludeType + '"]').click();

  // Wait for 1 second
  cy.wait(1000);

  // Find the div with class "execution-info", find the button data-tooltip "Refresh" and click it
  cy.get('.execution-info').find('[data-tooltip="Refresh"]').click();
}

// List of taskNames and concludeTypes
const tasks = [
  { taskName: '1: Empty', concludeType: 'Complete' },
  // { taskName: '2.1', concludeType: 'Complete' },
  // { taskName: '2.2', concludeType: 'Complete' },
  // { taskName: '3.1', concludeType: 'Complete' },
  // { taskName: '3.2', concludeType: 'Complete' },
  // { taskName: '3.3', concludeType: 'Complete' },
  // { taskName: '4.1', concludeType: 'Complete' },
  // { taskName: '4.2', concludeType: 'Complete' },
  // { taskName: '4.3', concludeType: 'Complete' },
  // { taskName: '4.4 IF', concludeType: 'Interrupt' },
  // { taskName: '5.1', concludeType: 'Interrupt' },
  // { taskName: '5.2', concludeType: 'Complete' },
  { taskName: 'Last: Execution Completed', concludeType: 'Complete' },
];




describe('template spec', () => {
  it('passes', () => {
    cy.visit('http://localhost:80')

    // Wait for 1 second
    cy.wait(1000);

    // Find the link with text "Sign up" and click it
    cy.get('a').contains('Sign up').click();

    // Wait for 1 second
    cy.wait(1000);

    // Create a username "TestUser" followed by a random number
    const username = 'TestUser' + Math.floor(Math.random() * 1000000);

    // Find the input with id "username" and type the username
    cy.get('input#username').type(username);

    // Create an email with the username and a domain
    const email = username + '@test.com';

    // Find the input with id "email" and type the email
    cy.get('input#email').type(email);

    // Find the input with id "password" and type "TestPassword1234!"
    const password = 'TestPassword1234!';
    cy.get('input#password').type(password);

    // Wait for 2 seconds
    cy.wait(2000);

    // Find the button with text "Sign Up" and click it
    cy.get('button').contains('Sign Up').click();

    // Wait for 1 second
    cy.wait(1000);

    // Find the input with id "email" and type the email
    cy.get('input#email').type(email);

    // Find the input with id "password" and type the password
    cy.get('input#password').type(password);

    // Wait for 2 seconds
    cy.wait(2000);

    // Find the button with text "Sign In" and click it
    cy.get('button').contains('Sign In').click();

    // Wait for 1 second
    cy.wait(1000);

    // Hover in the middle of div with class "popup-content" and scroll down
    cy.get('.popup-content').scrollIntoView().scrollTo('bottom');

    // Wait for 1 second
    cy.wait(1000);

    // Find the button with id "btn-accept" and click it
    cy.get('button#btn-accept').click();

    // Wait for 1 second
    cy.wait(1000);

    // Hover over item with class "library-icon"
    cy.get('.library-icon').trigger('mouseover').click();

    // Wait for 1 second
    cy.wait(1000);

    // Find the div with class "new-button", find the first button within and click it
    cy.get('.new-button').find('button').first().click();

    // Wait for 1 second
    cy.wait(1000);

    // Find input with id "name" and type "TestProcess"
    cy.get('input#name').type('TestProcess');

    // Select the file input and attach a file
    const filePath = './TestProcess.json'; 
    cy.get('input[type="file"]').attachFile(filePath);

    // Find text area with id "description" and type "TestProcess Description"
    cy.get('textarea#description').type('TestProcess Description');

    // Find select with id "visibility" and select "Public"
    cy.get('select#visibility').select('Public');

    // Find selest with id "pricingType" and select "Pay once"
    cy.get('select#pricingType').select('Pay once');

    // Find input with id "price" and type "100"
    cy.get('input#price').type('100');

    // Wait for 2 second
    cy.wait(2000);

    // Find only button within div with class "popup-content" and click it
    cy.get('.popup-content').find('button').click();

    // Wait for 2 seconds, reload and wait for 1 second
    cy.wait(2000);
    cy.reload();
    cy.wait(1000);

    // Hover over item with class "library-icon"
    cy.get('.library-icon').trigger('mouseover').click();

    // Wait for 1 second
    cy.wait(1000);

    // Find the first div with class "process-publishment" and click it
    cy.get('.process-publishment').first().click();

    // Wait for 1 second
    cy.wait(1000);

    // Find inside div with class "button-block" the button with text "+" and click it
    cy.get('.button-block').find('button').contains('+').click();

    // Wait for 1 second
    cy.wait(1000);

    // Find the last input with id "name" and type "TestExecution Version 2"
    cy.get('input#name').last().type('TestExecution Version 2');

    // Select the file input and attach a file
    const filePath2 = './TestProcess.json'; 
    cy.get('input[type="file"]').attachFile(filePath2);

    // Find the last text area with id "change-notes" and type "This is the second version and adds new features"
    cy.get('textarea#change-notes').last().type('This is the second version and adds new features');

    // Wait for 2 seconds
    cy.wait(2000);

    // Find the div with class "button-block", find the button data-tooltip "Create" and click it
    cy.get('.button-block').find('[data-tooltip="Create"]').click();

    // Wait for 1 second
    cy.wait(1000);

    // Hover in the middle of div with class "popup-content" and scroll down
    cy.get('.popup-content').scrollIntoView().scrollTo('bottom');

    // Wait for 1 second
    cy.wait(1000);

    // Find select with id "version" and select the last option
    cy.get('select#version').select('TestExecution Version 2');

    // Wait for 1 second
    cy.wait(1000);

    // Find the div with class "button-block", find the button data-tooltip "Execute Version" and click it
    cy.get('.button-block').find('[data-tooltip="Execute Version"]').click();

    // Wait for 2.5 seconds
    cy.wait(2500);

    // Hover over item with class "library-icon"
    cy.get('.library-icon').trigger('mouseover').click();

    // Find the div with class "execution-list", find the button data-tooltip "Refresh" and click it
    cy.get('.execution-list').find('[data-tooltip="Refresh"]').click();

    // Wait for 1 second
    cy.wait(1000);

    // Click on the first execution
    cy.get('.execution').first().click();

    // conclude all tasks
    tasks.forEach(task => {
      concludeTask(task.taskName, task.concludeType);
    });

    // Wait for 1 second
    cy.wait(1000);
    
    // Find the div with class "execution-info", find the button data-tooltip "Delete" and click it
    cy.get('.execution-info').find('[data-tooltip="Delete"]').click();

    // Wait for 1 second
    cy.wait(1000);

    // Hover over item with class "library-icon"
    cy.get('.library-icon').trigger('mouseover').click();

    // Wait for 1 second
    cy.wait(1000);

    // Find the first div with class "process-publishment" and click it
    cy.get('.process-publishment').first().click();

    // Wait for 1 second
    cy.wait(1000);

    // Find the div with class "button-block", find the button data-tooltip "Delete Version" and click it
    cy.get('.button-block').find('[data-tooltip="Delete Version"]').click();

    // Wait for 1 second
    cy.wait(1000);

    // Hover over item with class "library-icon"
    cy.get('.library-icon').trigger('mouseover').click();

    // Wait for 1 second
    cy.wait(1000);

    // Find the first div with class "process-publishment" and click it
    cy.get('.process-publishment').first().click();

    // Wait for 1 second
    cy.wait(1000);

    // Find the div with class "button-block", find the button data-tooltip "Delete Publishment" and click it
    cy.get('.button-block').find('[data-tooltip="Delete Publishment"]').click();

    // Reload
    cy.reload();

    // Hover over item with class "library-icon"
    cy.get('.library-icon').trigger('mouseover').click();

    // Wait for 1 second
    cy.wait(1000);

    // Reload
    cy.reload();

    // Hover over item with class "file-menu"
    cy.get('.file-menu').trigger('mouseover').click();

    // Wait for 1 second
    cy.wait(1000);

    // Find the div with class "dropdown-menu", find the button data-tooltip "Profile" and click it
    cy.get('.dropdown-menu').find('[data-tooltip="Profile"]').click();

    // Wait for 2 second
    cy.wait(2000);

    // Find the button with text "Delete Account" and click it
    cy.get('button').contains('Delete Account').click();
  })
})