package me.jho5245.mario.animations;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import me.jho5245.mario.components.Component;
import me.jho5245.mario.components.SpriteRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class StateMachine extends Component
{
	public HashMap<StateTrigger, String> stateTransfers = new HashMap<>();
	private List<AnimationState> states = new ArrayList<>();
	private transient AnimationState currentState;
	private String defaultStateTitle = "";

	public void refreshTextures()
	{
		for (AnimationState state : states)
		{
			state.refreshTextures();
		}
	}

	public void addStateTrigger(String from, String to, String onTrigger)
	{
		this.stateTransfers.put(new StateTrigger(from, onTrigger), to);
	}

	public void addState(AnimationState state)
	{
		this.states.add(state);
	}

	public void setDefaultStateTitle(String defaultStateTitle)
	{
		for (AnimationState state : this.states)
		{
			if (state.title.equals(defaultStateTitle))
			{
				this.defaultStateTitle = defaultStateTitle;
				if (currentState == null)
				{
					currentState = state;
					return;
				}
			}
		}
		System.out.println("Unable to find state: " + defaultStateTitle);
	}

	public void trigger(String trigger)
	{
		for (StateTrigger stateTrigger : this.stateTransfers.keySet())
		{
			if (stateTrigger.state.equals(currentState.title) && stateTrigger.trigger.equals(trigger))
			{
				if (stateTransfers.containsKey(stateTrigger))
				{
					int newStateIndex = -1;
					int index = 0;
					for (AnimationState state : this.states)
					{
						if (state.title.equals(stateTransfers.get(stateTrigger)))
						{
							newStateIndex = index;
							break;
						}
						index++;
					}
					if (newStateIndex > -1)
					{
						currentState = this.states.get(newStateIndex);
					}
				}
				return;
			}
		}
		System.out.println("unable to find trigger: " + trigger);
	}

	@Override
	public void start()
	{
		for (AnimationState state : this.states)
		{
			if (state.title.equals(this.defaultStateTitle))
			{
				currentState = state;
				break;
			}
		}
	}

	@Override
	public void update(float dt)
	{
		if (currentState != null)
		{
			currentState.update(dt);
			SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
			if (sprite != null)
			{
				sprite.setSprite(currentState.getCurrentSprite());
			}
		}
	}

	@Override
	public void editorUpdate(float dt)
	{
		if (currentState != null)
		{
			currentState.update(dt);
			SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
			if (sprite != null)
			{
				sprite.setSprite(currentState.getCurrentSprite());
			}
		}
	}

	@Override
	public void imgui()
	{
		int index = 0;
		for (AnimationState state : this.states)
		{
			ImString title = new ImString(state.title);
			ImGui.inputText("State: ", title);
			state.title = title.get();

			ImBoolean doesLoop = new ImBoolean(state.doesLoop);
			ImGui.checkbox("Loop: ", doesLoop);
			state.setDoesLoop(doesLoop.get());

			for (Frame frame : state.animationFrames)
			{
				float[] temp = { frame.frameTime };
				ImGui.dragFloat("Frame(%s) Time: ".formatted(index), temp, 0.01f);
				frame.frameTime = temp[0];
				index++;
			}
		}
	}

	private class StateTrigger
	{
		public String state;
		public String trigger;

		public StateTrigger()
		{

		}

		public StateTrigger(String state, String trigger)
		{
			this.state = state;
			this.trigger = trigger;
		}

		@Override
		public boolean equals(Object o)
		{
			return o instanceof StateTrigger stateTrigger && Objects.equals(stateTrigger.state, this.state) && Objects.equals(stateTrigger.trigger, this.trigger);
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(state, trigger);
		}
	}
}
