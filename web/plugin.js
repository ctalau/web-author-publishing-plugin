/**
 * Publishing action.
 */
PublishAction = function(editor) {
  sync.actions.AbstractAction.call(this);
  this.editor = editor;
};
PublishAction.prototype = Object.create(sync.actions.AbstractAction.prototype);
PublishAction.prototype.constructor = PublishAction;

PublishAction.prototype.getDisplayName = function() {
  return 'Generate PDF';
};

// The actual action execution.
PublishAction.prototype.actionPerformed = function(callback) {
  this.editor.getActionsManager().invokeOperation(
      'com.oxygenxml.webapp.publish.GeneratePDF', {}, function(err, result) {
    if (result) {
      // Download the Base64-encoded PDF.
      var linkSource = "data:application/pdf;base64," + result;
      var downloadLink = document.createElement("a");
      var fileName = "output.pdf";

      downloadLink.href = linkSource;
      downloadLink.download = fileName;
      downloadLink.click();
    }
    callback && callback();
  });
};

goog.events.listen(workspace, sync.api.Workspace.EventType.EDITOR_LOADED, function(e) {
    var editor = e.editor;
    // Register the newly created action.
    editor.getActionsManager().registerAction('publish', new PublishAction(editor));
    addToContextMenu(editor, 'publish');
});

function addToContextMenu(editor, actionId) {
  goog.events.listen(editor, sync.api.Editor.EventTypes.ACTIONS_LOADED, function(e) {
    var contextualItems = e.actionsConfiguration.contextualItems;
    if (contextualItems) {
      contextualItems.push({
        id: actionId,
        type: "action"
      });
    }
  });
}
