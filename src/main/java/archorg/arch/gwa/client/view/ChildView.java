package archorg.arch.gwa.client.view;

import it.celi.research.balrog.beacon.SimpleBeacon;
import it.celi.research.balrog.beacon.SimpleBeaconChange;
import it.celi.research.balrog.beacon.SimpleBeaconObserver;
import it.celi.research.balrog.beacon.SimpleBeaconReadable;
import it.celi.research.balrog.event.Observable;
import archorg.arch.gwa.client.join.StringToIntegerJoin;
import archorg.arch.gwa.client.serialization.StatefulAction;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ChildView
  extends
  Composite
{
  private VerticalPanel main = new VerticalPanel();

  private VerticalPanel results_panel = new VerticalPanel();

  private TextBox box = new TextBox();

  public ChildView(
    final SimpleBeacon<Integer> input,
    final StatefulAction action_curr,
    final StatefulAction action_next,
    final SimpleBeaconReadable<? extends Iterable<? extends Integer>> results)
  {
    StringToIntegerJoin.join(box,
      input);
    final Anchor link = new Anchor();
    link.setText("Calculate Next!");
    link.setHref(action_next.getResultingState().get());
    action_next.getResultingState()
      .subscribe(new SimpleBeaconObserver<String>()
      {
        @Override
        public
          void
          notice(
            Observable<? extends SimpleBeaconChange<? extends String>> observable,
            SimpleBeaconChange<? extends String> message)
        {
          link.setHref("#" + message.getNew());
        }
      });
    Button button = new Button("Calculate This!");
    button.addClickHandler(new ClickHandler()
    {
      @Override
      public void onClick(
        ClickEvent event)
      {
        action_curr.execute();
      }
    });
    results.subscribe(new SimpleBeaconObserver<Iterable<? extends Integer>>()
    {
      @Override
      public
        void
        notice(
          Observable<? extends SimpleBeaconChange<? extends Iterable<? extends Integer>>> o,
          SimpleBeaconChange<? extends Iterable<? extends Integer>> change)
      {
        results_panel.clear();
        for (Integer i : change.getNew())
        {
          String ss;
          if (i != null)
            ss = i.toString();
          else
            ss = "null?";
          Label lab = new Label(ss);
          results_panel.add(lab);
        }
      }
    });
    main.add(box);
    main.add(link);
    main.add(button);
    main.add(results_panel);
    initWidget(main);
  }
}
