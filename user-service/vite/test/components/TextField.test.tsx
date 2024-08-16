import {render, screen} from "@testing-library/react";
import {TextField} from "../../src/components/TextField.tsx";
import {useState} from "react";

const ControlledTextField = () => {
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
  test("render with required params", () => {
    // Given
    // When
    const component = render(<TextField name="name" label="Name" onChange={() => {}} />);

    // Then
    const inputEl = screen.getByLabelText("Name");
    expect(inputEl).toBeDefined();
    expect(inputEl.getAttribute("name")).toBe("name");
    expect(inputEl.getAttribute("type")).toBeNull();
    expect(inputEl.getAttribute("value")).toBeNull();
    expect(component).toMatchSnapshot();
  });

  test("render with placeholder property", () => {

  });

  test("render with fullWidth property", () => {

  });

  test("render with style property", () => {

  });


  test("render with style property", () => {

  });

  test("render controlled and change its value", () => {

  });
})