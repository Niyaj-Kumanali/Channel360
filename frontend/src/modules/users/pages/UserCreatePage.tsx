import React from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { ArrowLeft } from 'lucide-react';
import { createUserSchema, type CreateUserFormData } from '../schemas/user.schema';
import { useCreateUser } from '../hooks/useUsers';
import { Input } from '@/shared/components/ui/Input';
import { Button } from '@/shared/components/ui/Button';
import { Card, CardContent, CardHeader } from '@/shared/components/ui/Card';
import { apiService } from '@/shared/services/api.service';

export const UserCreatePage: React.FC = () => {
  const navigate = useNavigate();
  const createMutation = useCreateUser();

  const { data: rolesData } = useQuery({
    queryKey: ['roles'],
    queryFn: () => apiService.get<any[]>('/roles'),
  });

  const { register, handleSubmit, control, formState: { errors } } = useForm<CreateUserFormData>({
    resolver: zodResolver(createUserSchema),
    defaultValues: { roleIds: [] },
  });

  const onSubmit = (data: CreateUserFormData) => {
    createMutation.mutate(data, {
      onSuccess: () => navigate('/users'),
    });
  };

  const roles = rolesData?.data || [];

  return (
    <div>
      <div className="mb-6">
        <Button variant="ghost" onClick={() => navigate('/users')}>
          <ArrowLeft className="h-4 w-4 mr-2" />
          Back to Users
        </Button>
      </div>
      <Card className="max-w-2xl">
        <CardHeader>
          <h2 className="text-xl font-bold text-gray-900">Create User</h2>
          <p className="text-gray-500 text-sm mt-1">Add a new user to the system</p>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <Input
                id="employeeId"
                label="Employee ID"
                error={errors.employeeId?.message}
                {...register('employeeId')}
              />
              <Input
                id="email"
                label="Email"
                type="email"
                error={errors.email?.message}
                {...register('email')}
              />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <Input
                id="firstName"
                label="First Name"
                error={errors.firstName?.message}
                {...register('firstName')}
              />
              <Input
                id="lastName"
                label="Last Name"
                error={errors.lastName?.message}
                {...register('lastName')}
              />
            </div>
            <Input
              id="mobileNumber"
              label="Mobile Number"
              {...register('mobileNumber')}
            />
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Roles</label>
              <Controller
                name="roleIds"
                control={control}
                render={({ field }) => (
                  <div className="space-y-2">
                    {roles.map((r: any) => (
                      <label key={r.id} className="flex items-center gap-2 cursor-pointer">
                        <input
                          type="checkbox"
                          value={r.id}
                          checked={field.value.includes(r.id)}
                          onChange={(e) => {
                            if (e.target.checked) {
                              field.onChange([...field.value, r.id]);
                            } else {
                              field.onChange(field.value.filter((id: number) => id !== r.id));
                            }
                          }}
                          className="rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
                        />
                        <span className="text-sm text-gray-700">{r.name}</span>
                      </label>
                    ))}
                  </div>
                )}
              />
              {errors.roleIds?.message && (
                <p className="mt-1 text-sm text-red-600">{errors.roleIds.message}</p>
              )}
            </div>
            <div className="flex justify-end gap-3 pt-4">
              <Button variant="outline" onClick={() => navigate('/users')}>Cancel</Button>
              <Button type="submit" isLoading={createMutation.isPending}>Create User</Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};
