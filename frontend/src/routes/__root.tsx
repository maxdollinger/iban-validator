import { createRootRoute, Link, Outlet } from "@tanstack/react-router";
import { TanStackRouterDevtools } from "@tanstack/router-devtools";

export const Route = createRootRoute({
	component: () => (
		<>
			<nav
				style={{
					display: "flex",
					gap: "1rem",
					padding: "1rem",
					borderBottom: "1px solid #eee",
				}}
			>
				<Link to="/" activeOptions={{ exact: true }}>
					Home
				</Link>
				<Link to="/bank">Bank</Link>
			</nav>
			<main style={{ padding: "1rem" }}>
				<Outlet />
			</main>
			<TanStackRouterDevtools />
		</>
	),
});
