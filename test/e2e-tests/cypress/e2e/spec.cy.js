// function to conclude a task
function concludeTask(taskName, concludeType) {
  // Wait for 1 second
  cy.wait(1000);

  // Click on the element with class '.task-bar' that has a span text of taskName
  cy.get('.task-bar').contains(taskName).click();

  // Wait for 1 second
  cy.wait(1000);

  // Click on the button with data-tooltip concludeType
  cy.get('[data-tooltip="' + concludeType + '"]').click();

  // Wait for 1 second1 and reload
  cy.reload();
  cy.wait(1000);
}

// List of taskNames and concludeTypes
const tasks = [
  { taskName: '1: Empty', concludeType: 'Complete' },
  { taskName: '2.1', concludeType: 'Complete' },
  { taskName: '2.2', concludeType: 'Complete' },
  { taskName: '3.1', concludeType: 'Complete' },
  { taskName: '3.2', concludeType: 'Complete' },
  { taskName: '3.3', concludeType: 'Complete' },
  { taskName: '4.1', concludeType: 'Complete' },
  { taskName: '4.2', concludeType: 'Complete' },
  { taskName: '4.3', concludeType: 'Complete' },
  { taskName: '4.4 IF', concludeType: 'Interrupt' },
  { taskName: '5.1', concludeType: 'Interrupt' },
  { taskName: '5.2', concludeType: 'Complete' },
  { taskName: 'Last: Execution Completed', concludeType: 'Complete' },
];




describe('template spec', () => {
  it('passes', () => {
    cy.visit('http://localhost:80')

    // Wait for 1 second
    cy.wait(1000);

    // Select the file input and attach a file
    const filePath = '../../../test-data/TestProcess.json'; 
    cy.get('input[type="file"]').attachFile(filePath);

    // Wait for 3 seconds, reload and wait for 1 second
    cy.wait(3000);
    cy.reload();
    cy.wait(1000);

    // Click on the first execution
    cy.get('.execution').first().click();

    // conclude all tasks
    tasks.forEach(task => {
      concludeTask(task.taskName, task.concludeType);
    });
  })
})