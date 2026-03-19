import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/bank")({
	component: About,
});

function About() {
	return (
		<div>
			<h1>About</h1>
			<p>
				This is a React SPA built with Vite, TanStack Router, and TanStack
				Query.
			</p>
		</div>
	);
}
