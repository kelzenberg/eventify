import React from 'react';
import {render} from '@testing-library/react';
import "@testing-library/jest-dom/extend-expect";
import Title from "./Title";
import { MemoryRouter } from 'react-router-dom';

test('Title', () => {
    // MemoryRouter is needed for the <Link/> components
    let { container, rerender } = render(
        <MemoryRouter>
            <Title title="Test Title" breadcrumbs={["one", {name: "two", link: "/linkToTwo"}, undefined]}>
                <span>child content</span>
            </Title>
        </MemoryRouter>
    );
    // This component is so simple a single snapshot is enough
    expect(container).toMatchSnapshot();

    // without breadcrumbs
    rerender(
        <MemoryRouter>
            <Title title="Test Title">
                <span>child content</span>
            </Title>
        </MemoryRouter>
    );
    expect(container).toMatchSnapshot();
})