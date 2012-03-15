package archorg.arch.gwa.client.view;

import it.celi.research.balrog.beacon.SimpleBeacon;
import it.celi.research.balrog.beacon.SimpleBeaconChange;
import it.celi.research.balrog.beacon.SimpleBeaconObserver;
import it.celi.research.balrog.beacon.SimpleBeaconReadable;
import it.celi.research.balrog.event.Observable;
import archorg.arch.gwa.client.join.EquiJoin;
import archorg.arch.gwa.client.model.ChildModel;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RootView
  extends
  Composite
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

  public RootView(
    final SimpleBeacon<Boolean> has_child,
    final SimpleBeaconReadable<? extends ChildModel> child,
    final SimpleBeaconReadable<? extends String> error)
  {
    error.subscribe(new SimpleBeaconObserver<String>()
    {
      @Override
      public void notice(
        Observable<? extends SimpleBeaconChange<? extends String>> o,
        SimpleBeaconChange<? extends String> change)
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
    child.subscribe(new SimpleBeaconObserver<ChildModel>()
    {
      @Override
      public void notice(
        Observable<? extends SimpleBeaconChange<? extends ChildModel>> o,
        SimpleBeaconChange<? extends ChildModel> change)
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
            new ChildView(model.input, model.getActionCurrent(), model
              .getActionNext(), model.results);
        }
        redraw();
      }
    });
    com.google.gwt.user.client.ui.HasValue<Boolean> hvb = childbox;
    it.celi.research.balrog.beacon.SimpleBeacon<Boolean> bea = has_child;
    EquiJoin.join(hvb,
      bea);
    if (child.isNotNull())
    {
      ChildModel model = child.get();
      childview =
        new ChildView(model.input, model.getActionCurrent(),
          model.getActionNext(), model.results);
    }
    redraw();
    initWidget(main);
  }
}
