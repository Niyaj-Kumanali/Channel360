export interface MenuItem {
  path: string;
  label: string;
  icon: string;
  roles: string[];
  children?: MenuItem[];
}
