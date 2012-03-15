package archorg.arch.gwa.client;

import it.celi.research.balrog.event.Observable;
import it.celi.research.balrog.event.Observer;
import archorg.arch.gwa.client.model.RootModel;
import archorg.arch.gwa.client.serialization.StatefulActionImpl;
import archorg.arch.gwa.client.uristate.URIFragmentStateManager;
import archorg.arch.gwa.client.view.RootView;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

public class Client
  implements
  EntryPoint
{
  public static final ServiceAsync stub = GWT.create(Service.class);

  @Override
  public void onModuleLoad()
  {
    // create the URI state manager. it loads the application state from the URI
    URIFragmentStateManager sm = new URIFragmentStateManager();
    // // TriggerBeacons can be configured to trigger a dump of the current
    // state
    StatefulActionImpl.setStateManager(sm);
    // create the model
    final RootModel rm = new RootModel(sm.getEnvironmentChangeObserver());
    sm.setRoot(rm);
    // errors in the loading are routed using this observer
    sm.getMessage().subscribe(new Observer<String>()
    {
      @Override
      public void notice(
        Observable<? extends String> observable,
        String message)
      {
        rm.getMessageBW().setNevertheless(message);
      }
    });
    // create the view, connected to the model
    RootView rv =
      new RootView(rm.getHasChildB(), rm.getChildBR(), rm.getMessageBR());
    // publish the view
    RootPanel.get().add(rv);
  }
}
