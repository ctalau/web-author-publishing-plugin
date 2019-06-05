package com.oxygenxml.webapp.publish;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import ro.sync.ecss.extensions.api.ArgumentDescriptor;
import ro.sync.ecss.extensions.api.ArgumentsMap;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.webapp.AuthorDocumentModel;
import ro.sync.ecss.extensions.api.webapp.AuthorOperationWithResult;
import ro.sync.ecss.extensions.api.webapp.WebappRestSafe;

/**
 * Generate a PDF by calling a REST service.
 * 
 * @author cristi_talau
 */
@WebappRestSafe
public class GeneratePDF extends AuthorOperationWithResult {

  @Override
  public String getDescription() {
    return "Generate PDF";
  }

  public ArgumentDescriptor[] getArguments() {
    return new ArgumentDescriptor[0];
  }

  @Override
  public String doOperation(AuthorDocumentModel model, ArgumentsMap args)
      throws AuthorOperationException {
    String serviceUrl = "https://xn3yxc43g9.execute-api.us-west-2.amazonaws.com/default/xmlpaper";
    try (InputStream contentInputStream = model.getAuthorAccess().getEditorAccess().createContentInputStream()) {
      URLConnection connection = new URL(serviceUrl).openConnection();
      ((HttpURLConnection)connection).setReadTimeout(30000);
      
      connection.setDoOutput(true);
      try (OutputStream os = connection.getOutputStream()) {
        IOUtils.copy(contentInputStream, os);
      }
      
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      try (InputStream result = connection.getInputStream()) {
        IOUtils.copy(result, buffer);
      }
      return new String(buffer.toByteArray(), StandardCharsets.US_ASCII);
    } catch (IOException e) {
      throw new AuthorOperationException("Could not generate PDF", e);
    }
  }
}
