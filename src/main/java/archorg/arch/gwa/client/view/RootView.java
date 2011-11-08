package archorg.arch.gwa.client.view;

import it.celi.research.balrog.beacon.Beacon;
import it.celi.research.balrog.beacon.BeaconReader;
import it.celi.research.balrog.beacon.Change;
import it.celi.research.balrog.claudenda.Claudenda;
import it.celi.research.balrog.claudenda.Claudendum;
import it.celi.research.balrog.event.Observable;
import archorg.arch.gwa.client.ClaudendumBeaconObserver;
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

  private Claudenda childview_claudenda = null;

  private void redraw()
  {
    main.clear();
    main.add(childbox);
    if (childview != null)
      main.add(childview);
    if (errorMessage != null)
      main.add(errorMessage);
  }

  public RootView(Claudenda clau,
      final Beacon<Boolean> has_child,
      final BeaconReader<? extends ChildModel> child,
      final BeaconReader<? extends String> error)
  {
    error.subscribe(new ClaudendumBeaconObserver<String>(clau)
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
    child.subscribe(new ClaudendumBeaconObserver<ChildModel>(clau)
    {
      @Override
      public void notice(Observable<? extends Change<? extends ChildModel>> o,
          Change<? extends ChildModel> change)
      {
        if (change.newEqualsOld())
          return;
        if (change.newIsNull())
        {
          if (childview_claudenda != null)
            childview_claudenda.closeAll();
          childview = null;
        } else
        {
          ChildModel model = change.getNew();
          childview_claudenda = new Claudenda(getClass(), "child_view");
          childview =
            new ChildView(childview_claudenda, model.input, model.ask,
              model.results);
        }
        redraw();
      }
    });
    EquiJoin.join(clau,
      childbox,
      has_child);
    clau.add(new Claudendum()
    {
      @Override
      public void close() throws Exception
      {
        if (childview_claudenda != null)
          childview_claudenda.closeAll();
      }
    });
    if (child.isNotNull())
    {
      ChildModel model = child.get();
      childview_claudenda = new Claudenda(getClass(), "child_view");
      childview =
        new ChildView(childview_claudenda, model.input, model.ask,
          model.results);
    }
    redraw();
    initWidget(main);
  }
}
