package me.jho5245.mario.editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class JImGui
{
	private static float defaultColumnWidth = 120f;

	public static void drawVec2Control(String label, Vector2f values)
	{
		drawVec2Control(label, values, 0f);
	}

	public static void drawVec2Control(String label, Vector2f values, float resetValue)
	{
		drawVec2Control(label, values, resetValue, defaultColumnWidth);
	}

	public static void drawVec2Control(String label, Vector2f values, float resetValue, float columnWidth)
	{
		ImGui.pushID(label);

		ImGui.columns(2);
		ImGui.setColumnWidth(0, columnWidth);
		ImGui.text(label);

		ImGui.nextColumn();
		ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
		float lineHeight = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY();
		Vector2f buttonSize = new Vector2f(lineHeight + 3f, lineHeight);
		float widthEach = (ImGui.calcItemWidth() - buttonSize.x * 2f) / 2f;

		ImGui.pushItemWidth(widthEach);
		ImGui.pushStyleColor(ImGuiCol.Button, .8f, .1f, .2f, 1f);
		ImGui.pushStyleColor(ImGuiCol.ButtonHovered, .8f, .3f, .3f, 1f);
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, .8f, .1f, .2f, 1f);
		if (ImGui.button("X", buttonSize.x, buttonSize.y))
		{
			values.x = resetValue;
		}
		ImGui.popStyleColor(3);

		ImGui.sameLine();
		float[] vecValuesX = {values.x};
		ImGui.dragFloat("##x", vecValuesX, 0.1f);
		values.x = vecValuesX[0];
		ImGui.popItemWidth();

		ImGui.sameLine();
		ImGui.pushItemWidth(widthEach);
		ImGui.pushStyleColor(ImGuiCol.Button, .2f, .7f, .2f, 1f);
		ImGui.pushStyleColor(ImGuiCol.ButtonHovered, .3f, .8f, .3f, 1f);
		ImGui.pushStyleColor(ImGuiCol.ButtonActive, .2f, .7f, .2f, 1f);
		if (ImGui.button("Y", buttonSize.x, buttonSize.y))
		{
			values.y = resetValue;
		}
		ImGui.popStyleColor(3);

		ImGui.sameLine();
		float[] vecValuesY = {values.y};
		ImGui.dragFloat("##y", vecValuesY, 0.1f);
		values.y = vecValuesY[0];
		ImGui.popItemWidth();
		ImGui.sameLine();

		ImGui.nextColumn();
		ImGui.popStyleVar();

		ImGui.columns(1);

		ImGui.popID();
	}

	public static float dragFloat(String label, float value)
	{
		ImGui.pushID(label);

		ImGui.columns(2);
		ImGui.setColumnWidth(0, defaultColumnWidth);
		ImGui.text(label);
		ImGui.nextColumn();

		float[] valArr = {value};
		ImGui.dragFloat("##dragFloat", valArr, 0.1f);

		ImGui.columns(1);
		ImGui.popID();

		return valArr[0];
	}

	public static int dragInt(String label, int value)
	{
		ImGui.pushID(label);

		ImGui.columns(2);
		ImGui.setColumnWidth(0, defaultColumnWidth);
		ImGui.text(label);
		ImGui.nextColumn();

		int[] valArr = {value};
		ImGui.dragInt("##dragFloat", valArr, 0.1f);

		ImGui.columns(1);
		ImGui.popID();

		return valArr[0];
	}
	public static boolean colorPicker4(String label, Vector4f color)
	{
		boolean result = false;
		ImGui.pushID(label);

		ImGui.columns(2);
		ImGui.setColumnWidth(0, defaultColumnWidth);
		ImGui.text(label);
		ImGui.nextColumn();

		float[] imColor = {color.x, color.y, color.z, color.w};
		if (ImGui.colorEdit4("##colorPicker", imColor))
		{
			color.set(imColor[0], imColor[1], imColor[2], imColor[3]);
			result = true;
		}

		ImGui.columns(1);
		ImGui.popID();

		return result;
	}
}
