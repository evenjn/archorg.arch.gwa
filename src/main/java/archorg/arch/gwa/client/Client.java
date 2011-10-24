package archorg.arch.gwa.client;

import archorg.arch.gwa.client.model.RootModel;
import archorg.arch.gwa.client.view.RootView;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;

public class Client implements EntryPoint
{
  @Override
  public void onModuleLoad()
  {
    History.addValueChangeHandler(new ValueChangeHandler<String>()
    {
      public void onValueChange(ValueChangeEvent<String> event)
      {
        String historyToken = event.getValue();
        processHistoryToken(historyToken);
      }
    });
    String token = History.getToken();
    if (token.length() != 0)
      processHistoryToken(token);
    RootModel rm = new RootModel();
    RootView rv = new RootView(rm.input, rm.ask, rm.message, rm.results);
    RootPanel.get().add(rv);
  }

  private void processHistoryToken(String historyToken)
  {
    String decode = URL.decodeQueryString(historyToken);
  }
}
