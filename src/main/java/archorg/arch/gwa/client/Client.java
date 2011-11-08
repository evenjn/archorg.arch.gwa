package archorg.arch.gwa.client;

import it.celi.research.balrog.claudenda.Claudenda;
import it.celi.research.balrog.claudenda.ClaudendaExceptionHandler;
import it.celi.research.balrog.claudenda.ClaudendaForgottenHandler;
import archorg.arch.gwa.client.model.RootModel;
import archorg.arch.gwa.client.serialization.URIStateManager;
import archorg.arch.gwa.client.view.RootView;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

public class Client implements EntryPoint
{
  public static final ServiceAsync stub = GWT.create(Service.class);

  @Override
  public void onModuleLoad()
  {
    // we are never going to get rid of the root model, but all models have to
    // be closed with claudenda in order to unsubscribe the observers from the
    // event channel that broadcasts the end of the state loading process in
    // order to allow the restoration of non-transient state after the whole
    // model has been loaded.
    //
    // we are never going to get rid of the rootview, but all views have to be
    // closed with claudenda in order to unsubscribe the observers from the
    // model
    Claudenda clau = new Claudenda(getClass());
    // create the model
    final RootModel rm = new RootModel(clau);
    // load the model state from the URL
    URIStateManager.init(rm,
      rm.getMessageBW());
    // create the view, connected to the model
    RootView rv =
      new RootView(clau, rm.getHasChildB(), rm.getChildBR(), rm.getMessageBR());
    Claudenda.setExceptionHandler(new ClaudendaExceptionHandler()
    {
      @Override
      public void handle(Claudenda clau,
          Exception e)
      {
        rm.getMessageBW()
          .setNevertheless("Exception caught when closing claudenda.");
      }
    });
    Claudenda.setForgottenHandler(new ClaudendaForgottenHandler()
    {
      @Override
      public void onForgotten(Claudenda clau)
      {
        rm.getMessageBW().setNevertheless("Forgot to close claudenda. "
            + clau.getDescriptionForDeveloper());
      }
    });
    // publish the view
    RootPanel.get().add(rv);
  }
}
