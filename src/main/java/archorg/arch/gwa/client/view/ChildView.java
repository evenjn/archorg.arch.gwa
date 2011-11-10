package archorg.arch.gwa.client.view;

import it.celi.research.balrog.beacon.Beacon;
import it.celi.research.balrog.beacon.BeaconObserver;
import it.celi.research.balrog.beacon.BeaconReader;
import it.celi.research.balrog.beacon.BeaconWriter;
import it.celi.research.balrog.beacon.Change;
import it.celi.research.balrog.event.Observable;
import archorg.arch.gwa.client.join.StringToIntegerJoin;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ChildView extends Composite
{
  private VerticalPanel main = new VerticalPanel();

  private VerticalPanel results_panel = new VerticalPanel();

  private TextBox box = new TextBox();

  public ChildView(final Beacon<Integer> input,
      final BeaconWriter<Void> go,
      final BeaconReader<? extends Iterable<? extends Integer>> results)
  {
    StringToIntegerJoin.join(box,
      input);
    Button button = new Button("Calculate!");
    button.addClickHandler(new ClickHandler()
    {
      @Override
      public void onClick(ClickEvent event)
      {
        go.setNevertheless(null);
      }
    });
    results.subscribe(new BeaconObserver<Iterable<? extends Integer>>()
    {
      @Override
      public
          void
          notice(Observable<? extends Change<? extends Iterable<? extends Integer>>> o,
              Change<? extends Iterable<? extends Integer>> change)
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
    main.add(button);
    main.add(results_panel);
    initWidget(main);
  }
}
