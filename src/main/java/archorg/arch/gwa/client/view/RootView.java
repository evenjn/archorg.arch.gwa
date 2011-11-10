package archorg.arch.gwa.client.view;

import it.celi.research.balrog.beacon.Beacon;
import it.celi.research.balrog.beacon.BeaconObserver;
import it.celi.research.balrog.beacon.BeaconReader;
import it.celi.research.balrog.beacon.Change;
import it.celi.research.balrog.event.Observable;
import archorg.arch.gwa.client.join.EquiJoin;
import archorg.arch.gwa.client.model.ChildModel;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RootView extends Composite
{
  private VerticalPanel main = new VerticalPanel();

  private Label errorMessage = null;

  private CheckBox childbox = new CheckBox("Show child");

  private ChildView childview = null;

  private void redraw()
  {
    main.clear();
    main.add(childbox);
    if (childview != null)
      main.add(childview);
    if (errorMessage != null)
      main.add(errorMessage);
  }

  public RootView(final Beacon<Boolean> has_child,
      final BeaconReader<? extends ChildModel> child,
      final BeaconReader<? extends String> error)
  {
    error.subscribe(new BeaconObserver<String>()
    {
      @Override
      public void notice(Observable<? extends Change<? extends String>> o,
          Change<? extends String> change)
      {
        if (change.newEqualsOld())
          return;
        if (change.newIsNull())
        {
          errorMessage = null;
        } else
        {
          if (errorMessage == null)
            errorMessage = new Label();
          errorMessage.setText(change.getNew());
        }
        redraw();
      }
    });
    child.subscribe(new BeaconObserver<ChildModel>()
    {
      @Override
      public void notice(Observable<? extends Change<? extends ChildModel>> o,
          Change<? extends ChildModel> change)
      {
        if (change.newEqualsOld())
          return;
        if (change.newIsNull())
        {
          childview = null;
        } else
        {
          ChildModel model = change.getNew();
          childview =
            new ChildView(model.input, model.getActionW(), model.results);
        }
        redraw();
      }
    });
    EquiJoin.join(childbox,
      has_child);
    if (child.isNotNull())
    {
      ChildModel model = child.get();
      childview = new ChildView(model.input, model.getActionW(), model.results);
    }
    redraw();
    initWidget(main);
  }
}
