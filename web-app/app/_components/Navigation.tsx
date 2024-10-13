"use client";

import {usePathname} from "next/navigation";
import Link from "next/link";

const Navigation = () => {
  const isNumeric = (num: any) => (typeof(num) === 'number' || typeof(num) === "string" && num.trim() !== '') && !isNaN(num as number);
  const toPathName = (segment: string) => {
    if (isNumeric(segment)) {
      return "View";
    }

    const name = segment.replace("-", " ").trim();
    return name.charAt(0).toUpperCase() + name.slice(1);
  }

  const pathname = usePathname();

  const navItems = new Array<{ name: string; path: string, active: boolean }>();
  const isHomeActive = pathname.length === 1;
  navItems.push({ name: "Home", path: "/", active: isHomeActive });

  if (!isHomeActive) {
    let lastIndexOfSeparator = 0;
    let indexOfSeparator = 0;
    do {
      indexOfSeparator = pathname.indexOf("/", lastIndexOfSeparator + 1);
      const segmentLastPos = indexOfSeparator !== -1 ? indexOfSeparator : pathname.length;
      const segment = pathname.slice(lastIndexOfSeparator + 1, segmentLastPos);
      navItems.push({ name: toPathName(segment), path: pathname.slice(0, segmentLastPos), active: indexOfSeparator === -1 });
      lastIndexOfSeparator = indexOfSeparator;
    } while (indexOfSeparator !== -1)
  }

  return (
    <nav className="m-3">
      <ol className="breadcrumb">
        {navItems.map(navItem => {
          return navItem.active ? (
            <li key={navItem.path} className="breadcrumb-item active">
              {navItem.name}
            </li>
          ) : (
            <li key={navItem.path} className="breadcrumb-item">
              <Link href={navItem.path}>{navItem.name}</Link>
            </li>
          );
        })}
      </ol>
    </nav>
  );
};

export {Navigation};