import { createRootRoute, Link, Outlet } from "@tanstack/react-router";
import { TanStackRouterDevtools } from "@tanstack/router-devtools";

export const Route = createRootRoute({
	component: () => (
		<>
			<nav className="flex gap-4 px-6 py-3 border-b border-border">
				<Link
					to="/"
					activeOptions={{ exact: true }}
					className="text-muted-foreground hover:text-foreground transition-colors [&.active]:text-foreground [&.active]:font-medium"
				>
					Home
				</Link>
				<Link
					to="/bank"
					className="text-muted-foreground hover:text-foreground transition-colors [&.active]:text-foreground [&.active]:font-medium"
				>
					Bank
				</Link>
			</nav>
			<main className="p-6">
				<Outlet />
			</main>
			<TanStackRouterDevtools />
		</>
	),
});
