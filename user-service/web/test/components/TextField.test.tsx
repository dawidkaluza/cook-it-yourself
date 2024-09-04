import {render, screen} from "@testing-library/react";
import {TextField} from "../../src/components/TextField.tsx";
import {useState} from "react";
import {expect} from "vitest";
import {userEvent} from "@testing-library/user-event";

vi.mock("react", async function <T>(importOriginal: () => Promise<T>) {
  const original = await importOriginal();
  return {
    ...original,
    useId: () => "1234",
  };
});

const ControlledNameField = () => {
  const [name, setName] = useState("");

  return (
    <TextField
      name={"name"}
      label={"Name"}
      value={name}
      onChange={(event) => setName(event.target.value)}
    />
  );
};

describe("TextField component", () => {
  test("render with required props", () => {
    // Given
    // When
    const component = render(<TextField name="name" label="Name" onChange={() => {}} />);

    // Then
    const inputEl = screen.getByLabelText("Name");
    expect(inputEl).toBeDefined();
    expect(inputEl.getAttribute("name")).toBe("name");
    expect(inputEl.getAttribute("type")).toBeNull();
    expect(inputEl.getAttribute("value")).toBe("");
    expect(component).toMatchSnapshot();
  });

  test.each([
    ["text"],
    ["password"],
  ])("render with type %s and all other props", (type) => {
    // Given
    // When
    const component = render(
      <TextField
        name="name" label="Name"
        type={type as "password" | "text"} value="Dawid"
        error="Invalid value"
        onChange={() => {}}
        placeholder="Dawid" style="w-full"
      />
    );

    // Then
    const inputEl = screen.getByLabelText("Name");
    expect(inputEl).toBeDefined();
    expect(inputEl.getAttribute("name")).toBe("name");
    expect(inputEl.getAttribute("type")).toBe(type);
    expect(inputEl.getAttribute("value")).toBe("Dawid");
    expect(inputEl.getAttribute("placeholder")).toBe("Dawid");

    const errorEl = screen.getByText("Invalid value");
    expect(errorEl).toBeDefined();

    const containerEl = inputEl.closest("div") as HTMLDivElement;
    expect(containerEl).not.toBeNull();
    expect(containerEl.getAttribute("class")).toContain("w-full");

    expect(component.container).toMatchSnapshot();
  });

  test("render controlled and change its value", async () => {
    // Given
    const user = userEvent.setup();
    const component = render(<ControlledNameField />);
    const inputEl = screen.getByLabelText("Name");

    // When
    await user.click(inputEl);
    await user.keyboard("foo");

    // Then
    expect(inputEl.getAttribute("value")).toBe("foo");
    expect(component.container).toMatchSnapshot();
  });
})