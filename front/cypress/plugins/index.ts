/**
 * @type {Cypress.PluginConfig}
 */
// eslint-disable-next-line @typescript-eslint/no-var-requires
const registerCodeCoverageTasks = require('@cypress/code-coverage/task');

export default (on: Cypress.PluginEvents, config: Cypress.PluginConfigOptions) => {
  return registerCodeCoverageTasks(on, config);
};
