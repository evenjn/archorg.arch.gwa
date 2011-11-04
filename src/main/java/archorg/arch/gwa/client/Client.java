package archorg.arch.gwa.client;

import archorg.arch.gwa.client.model.RootModel;
import archorg.arch.gwa.client.serialization.SDeserialization;
import archorg.arch.gwa.client.serialization.SSerializationFormatException;
import archorg.arch.gwa.client.serialization.SSerializer;
import archorg.arch.gwa.client.view.RootView;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;

public class Client implements EntryPoint
{
  RootModel rm;

  @Override
  public void onModuleLoad()
  {
    rm = new RootModel();
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
    RootView rv = new RootView(rm.input, rm.ask, rm.message, rm.results);
    RootPanel.get().add(rv);
  }

  private void processHistoryToken(String historyToken)
  {
    String decode = URL.decodeQueryString(historyToken);
    try
    {
      SDeserialization load = SSerializer.load(decode);
      rm.load(load,
        load.getRootID(),
        true);
      rm.load(load,
        load.getRootID(),
        false);
    }
    catch (SSerializationFormatException e)
    {
      // reset to default?
    }
  }
}
